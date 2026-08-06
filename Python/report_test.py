# -*- coding: utf-8 -*-
"""
Created on Tue Nov 25 12:15:23 2025

@author: melli
"""

import os
import json
import pandas as pd
import asyncio
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

#from cai.sdk.config import set_config

# Configure CAI with model and API key
#set_config(
#    llm_provider=os.getenv("CAI_LLM_PROVIDER", "openai"),
 #   model=os.getenv("CAI_DEFAULT_MODEL", "gpt-4.1-mini"),
  #  api_key=os.getenv("OPENAI_API_KEY")
#)

from cai.sdk.agents import Runner
from cai.agents.reporter import reporting_agent  # your reporting agent

# === CONFIGURATION ===
EUREPOC_FILE = "/mnt/c/Users/melli/OneDrive/Dokumente/Semester 7/CAI/IR/eurepoc_data_2025_cleaned.xlsx"
RESULTS_JSONL = "/mnt/c/Users/melli/OneDrive/Dokumente/Semester 7/CAI/IR/results_structured_reports.jsonl"
ERROR_LOG = "/mnt/c/Users/melli/OneDrive/Dokumente/Semester 7/CAI/IR/error_log.txt"
REPORT_DIR = "/mnt/c/Users/melli/OneDrive/Dokumente/Semester 7/CAI/IR/reports"

RANDOM_STATE = 42
SLEEP_BETWEEN_CALLS = 1

os.makedirs(REPORT_DIR, exist_ok=True)

# --- Function to call agent ---
async def generate_report_text(incident_dict: dict) -> tuple:
    """
    Call the reporting_agent and parse JSON output safely.
    Returns (data, error)
    """
    try:
        # Use the class method Runner.run() as in your partner's working script
        raw_result = await Runner.run(reporting_agent, json.dumps(incident_dict), llm_provider="openai", model="gpt-4.1-mini",api_key=os.environ.get("OPENAI_API_KEY"))
        print(f"[DEBUG] raw_result: {raw_result}")

        if not raw_result:
            return None, "Agent returned None or empty output"

        text = str(raw_result)

        # Extract JSON from first { to last }
        start = text.index("{")
        end = text.rindex("}") + 1
        json_str = text[start:end]
        data = json.loads(json_str)

        # Optional: check if data is empty
        if not data:
            return None, "Agent returned empty JSON"

        return data, None
    except Exception as e:
        return None, str(e)

# --- Main async loop ---
async def main():
    df = pd.read_excel(EUREPOC_FILE)
    df['start_date'] = pd.to_datetime(df['start_date'], errors='coerce')
    df_2025 = df[df['start_date'].dt.year == 2025]
    df_2025 = df_2025.sample(frac=1, random_state=RANDOM_STATE).reset_index(drop=True)

    print(f"Total incidents to process: {len(df_2025)}")

    with open(RESULTS_JSONL, "w", encoding="utf-8") as results_file, \
         open(ERROR_LOG, "w", encoding="utf-8") as error_file:

        for idx, row in df_2025.iterrows():
            incident_dict = {
                "incident_id": row.get("ID", "Unknown"),
                "summary": {
                    "state": row.get("status", "Open"),
                    "priority": row.get("priority", "Unknown"),
                    "impact": row.get("impact", "Unknown"),
                    "urgency": row.get("urgency", "Unknown"),
                    "assignment_group": row.get("assignment_group", "Unknown"),
                    "responsible_analyst": row.get("responsible_analyst", "Unknown"),
                    "date_opened": pd.to_datetime(row.get("start_date"), errors="coerce").strftime("%Y-%m-%d")
                                   if pd.notnull(row.get("start_date")) else "Unknown",
                    "date_closed": pd.to_datetime(row.get("end_date"), errors="coerce").strftime("%Y-%m-%d")
                                   if pd.notnull(row.get("end_date")) else "Unknown"
                },
                "metadata": {
                    "report_type": "Eurepoc Cybersecurity Incident Report",
                    "version": "1.0",
                    "generated_by": "CAI reporting_agent",
                    "supervisor": "Melissa Zierer",
                    "date_generated": pd.Timestamp.now().strftime("%Y-%m-%d"),
                    "source_system": "Eurepoc Database"
                }
            }

            html_output, error = await generate_report_text(incident_dict)

            if html_output:
                out_path = os.path.join(REPORT_DIR, f"{row['ID']}.html")
                with open(out_path, "w", encoding="utf-8") as f:
                    f.write(json.dumps(html_output, indent=2))

                json.dump({"incident_id": row["ID"], "report_file": out_path}, results_file)
                results_file.write("\n")
                results_file.flush()

                print(f"[OK] Incident {row['ID']} saved.")
            else:
                error_file.write(f"{row['ID']}: {error}\n")
                error_file.flush()
                print(f"[ERROR] Incident {row['ID']} failed: {error}")

            await asyncio.sleep(SLEEP_BETWEEN_CALLS)

    # Final verification
    created_reports = os.listdir(REPORT_DIR)
    if created_reports:
        print(f"[INFO] {len(created_reports)} reports created successfully in '{REPORT_DIR}'")
    else:
        print(f"[WARNING] No reports were created. Check agent config, API key, or model.")

# --- Run ---
if __name__ == "__main__":
    asyncio.run(main())
