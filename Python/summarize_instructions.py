import os
import json
import asyncio

import pandas as pd
from dotenv import load_dotenv

from cai.sdk.agents import Runner
from cai.agents.reporter import reporting_agent


load_dotenv()

if not os.getenv("OPENAI_API_KEY"):
    raise RuntimeError("OPENAI_API_KEY ist nicht gesetzt. Bitte in .env eintragen.")


EUREPOC_FILE = r"/mnt/c/Users/melli/OneDrive/Dokumente/Semester 7/CAI/IR/data/kaggle_batch.csv"
RESULTS_JSONL = "/mnt/c/Users/melli/OneDrive/Dokumente/Semester 7/CAI/IR/results_structured_reports.jsonl"
ERROR_LOG = "/mnt/c/Users/melli/OneDrive/Dokumente/Semester 7/CAI/IR/error_log.txt"
REPORT_DIR = "/mnt/c/Users/melli/OneDrive/Dokumente/Semester 7/CAI/IR/reports"

RANDOM_STATE = 42
SLEEP_BETWEEN_CALLS = 1 

os.makedirs(REPORT_DIR, exist_ok=True)


async def generate_report_html(incident_dict: dict):
    
    try:
        prompt = f"""
            You are the CAI reporting_agent.
            Generate a Cybersecurity Incident Report for each incident based on the number in strict HTML format using the fixed structure and formatting shown below. 
            The data contains multiple incident records. The column "number" is the incident identifier. 
            Group all rows that share the same "number" and generate one complete HTML report for each unique 
            incident.
            Return ONLY valid HTML (no Markdown, no JSON, no backticks).
            For Executive Summary, generate a one sentence summary based on the data.
            For Technical Analysis, provide an analytical summary of the incident progression, number of updates, handlers, and state transitions.
            For Recommendations, provide a improvement recommendation each.
            
            Follow these rules:
            - DO NOT change the structure, section order, CSS, headings, table layouts, or wording.
            - Fill placeholders using the incident data provided after “INCIDENT DATA:”.
            - If a value is missing or null, replace it with: <span class="highlight">Unknown</span>.
            - TIMELINE_ROWS must contain <tr><td></td><td></td><td></td></tr> rows based on timeline events.
            - Do NOT remove sections or add new ones.
            - Produce only the completed HTML document.

            THE HTML TEMPLATE TO FILL:

            <!DOCTYPE html>
            <html lang="en">
            <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Cybersecurity Incident Report - INCIDENT_ID</title>
            <style>
            body {{ font-family: Arial, sans-serif; line-height: 1.6; }}
            h1, h2 {{ color: #333; }}
            table {{ width: 100%; border-collapse: collapse; margin: 20px 0; }}
            table, th, td {{ border: 1px solid #ddd; }}
            th, td {{ padding: 8px; text-align: left; }}
            th {{ background-color: #f2f2f2; }}
            .highlight {{ font-style: italic; color: red; }}
            </style>
            </head>
            <body>

            <h1>INCIDENT_ID</h1>

            <h2>Summary</h2>
            <table>
            <tr><th>Incident ID</th><td>INCIDENT_ID</td></tr>
            <tr><th>State</th><td>STATE</td></tr>
            <tr><th>Priority</th><td>PRIORITY</td></tr>
            <tr><th>Impact</th><td>IMPACT</td></tr>
            <tr><th>Urgency</th><td>URGENCY</td></tr>
            <tr><th>Assignment Group</th><td>ASSIGNMENT_GROUP</td></tr>
            <tr><th>Responsible Analyst</th><td>RESPONSIBLE_ANALYST</td></tr>
            <tr><th>Date Opened</th><td>DATE_OPENED</td></tr>
            <tr><th>Date Closed</th><td>DATE_CLOSED</td></tr>
            </table>

            <h2>Metadata</h2>
            <p><strong>Report_Type:</strong> REPORT_TYPE</p>
            <p><strong>Version:</strong> VERSION</p>
            <p><strong>Generated_By:</strong> GENERATED_BY</p>
            <p><strong>Supervisor:</strong> SUPERVISOR</p>
            <p><strong>Date_Generated:</strong> DATE_GENERATED</p>
            <p><strong>Source_System:</strong> SOURCE_SYSTEM</p>

            <h2>Incident Identification</h2>
            <p><strong>Incident_ID:</strong> INCIDENT_ID</p>
            <p><strong>State:</strong> STATE</p>
            <p><strong>Category / Subcategory:</strong> CATEGORY_SUBCATEGORY</p>
            <p><strong>Impact:</strong> IMPACT</p>
            <p><strong>Urgency:</strong> URGENCY</p>
            <p><strong>Priority:</strong> PRIORITY</p>
            <p><strong>Assignment_Group:</strong> ASSIGNMENT_GROUP</p>
            <p><strong>Responsible_Analyst:</strong> RESPONSIBLE_ANALYST</p>
            <p><strong>Affected_Asset (CMDB_CI):</strong> CONFIG_ITEM</p>
            <p><strong>Location:</strong> LOCATION</p>

            <h2>Executive Summary</h2>
            <p></p>

            <h2>Timeline of Events (Chronological)</h2>
            <table>
            <tr>
            <th>Timestamp</th>
            <th>State</th>
            <th>Responsible User</th>
            </tr>
            TIMELINE_ROWS
            </table>

            <h2>Incident Details</h2>
            <p><strong>Description_of_Symptom:</strong> SYMPTOM</p>
            <p><strong>Root_Cause:</strong> ROOT_CAUSE</p>
            <p><strong>Affected_Services:</strong> AFFECTED_SERVICES</p>
            <p><strong>User_Impact:</strong> USER_IMPACT</p>
            <p><strong>Detection_Method:</strong> DETECTION_METHOD</p>
            <p><strong>Triggering_Event:</strong> TRIGGER_EVENT</p>

            <h2>Technical Analysis</h2>
            <p></p>

            <h2>Resolution</h2>
            <p><strong>Resolution_Code:</strong> RESOLUTION_CODE</p>
            <p><strong>Days_Open:</strong> DAYS_OPEN</p>
            <p><strong>Days_Until_Resolution:</strong> DAYS_UNTIL_RESOLUTION</p>

            <h2>Impact Assessment</h2>
            <p><strong>Impact_Level:</strong> IMPACT</p>
            <p><strong>Affected_Users:</strong> AFFECTED_USERS</p>
            <p><strong>Service_Downtime:</strong> SERVICE_DOWNTIME</p>
            <p><strong>Operational_Effect:</strong> OPERATIONAL_EFFECT</p>

            <h2>Recommendations</h2>
            <ul>
            <li></li>
            <li></li>
            <li></li>
            </ul>

            <h2>Appendix</h2>
            <p>Structured data used for this report: <code>INCIDENT_ID dataset records</code></p>

            </body>
            </html>

            INCIDENT DATA:

            f"INCIDENT DATA:\n{json.dumps(incident_dict, indent=2)}
            """
        

        messages = [
            {"role": "user", "content": prompt}
        ]

        raw_result = await Runner.run(reporting_agent, messages)

        print("[DEBUG] raw_result type:", type(raw_result))
        print("[DEBUG] raw_result:", raw_result)

        if isinstance(raw_result, dict):
            if "output" in raw_result:
                html = raw_result["output"]
            elif "messages" in raw_result and raw_result["messages"]:
                last_msg = raw_result["messages"][-1]
                html = last_msg.get("content", "")
            else:
                html = str(raw_result)
        else:
            html = str(raw_result)

        if not html:
            raise ValueError("Empty HTML response from reporting_agent")

        return html, None

    except Exception as e:
        return None, repr(e)


async def main():
  
    df = pd.read_csv(EUREPOC_FILE, sep=";")

    
    df["opened_at_date"] = pd.to_datetime(df["opened_at_date"], dayfirst=True, errors="coerce")
    df["closed_at_date"] = pd.to_datetime(df["closed_at_date"], dayfirst=True, errors="coerce")

   
    YEAR_FILTER = 2016
    df_year = df[df["opened_at_date"].dt.year == YEAR_FILTER]
    df_year = df_year.sample(frac=1, random_state=RANDOM_STATE).reset_index(drop=True)

    with open(RESULTS_JSONL, "w", encoding="utf-8") as results_file, \
         open(ERROR_LOG, "w", encoding="utf-8") as error_file:

        for _, row in df_year.iterrows():
            incident_id = row.get("number", "Unknown")

            incident_dict = {
                    "incident_id": incident_id,
                "basic info":{
                    "incident_number": row.get("number"),
                    "active": row.get("active"),
                    "reassignment_count": row.get("reassignment_count"),
                    "reopen_count": row.get("reopen_count"),
                },
                "user info":{
                    "caller": row.get("Caller"),
                    "opened_by": row.get("opened_by"),
                    "contact_type": row.get("contact_type"),
                    "location": row.get("location"),
                },
                "assignment":{
                    "assignment_group": row.get("assignment_group"),
                    "assigned_to": row.get("assigned_to_resolver"),
                    "resolved_by": row.get("resolved_by"),
                },
                "categorization":{
                    "category": row.get("category"),
                    "subcategory": row.get("subcategory"),
                    "symptom": row.get("u_symptom"),
                    "configuration_item": row.get("cmdb_ci"),
                },
                "summary": {
                    "state": row.get("incident_state", "Open"),
                    "priority": row.get("priority_text", "Unknown"),
                    "impact": row.get("impact_text", "Unknown"),
                    "urgency": row.get("urgency_text", "Unknown"),
                    "assignment_group": row.get("assignment_group", "Unknown"),
                    "responsible_analyst": row.get("assigned_to_resolver", "Unknown"),
                    "date_opened": (
                        row["opened_at_date"].strftime("%Y-%m-%d")
                        if pd.notnull(row["opened_at_date"])
                        else "Unknown"
                    ),
                    "date_closed": (
                        row["closed_at_date"].strftime("%Y-%m-%d")
                        if pd.notnull(row["closed_at_date"])
                        else "Unknown"
                    ),
                },
                "metadata": {
                    "report_type": "Kaggle IT Incident Report",
                    "version": "1.0",
                    "generated_by": "CAI reporting_agent",
                    "supervisor": "Melissa Zierer",
                    "date_generated": pd.Timestamp.now().strftime("%Y-%m-%d"),
                    "source_system": "Kaggle IT Incident Log Dataset",
                },
            }

            html_output, error = await generate_report_html(incident_dict)

            if html_output is not None:
                out_path = os.path.join(REPORT_DIR, f"{incident_id}.html")
                with open(out_path, "w", encoding="utf-8") as f:
                    f.write(html_output)

                json.dump(
                    {"incident_id": incident_id, "report_file": out_path},
                    results_file,
                    ensure_ascii=False,
                )
                results_file.write("\n")
                results_file.flush()

                print(f"[OK] Incident {incident_id} saved → {out_path}")
            else:
                error_file.write(f"{incident_id}: {error}\n")
                error_file.flush()
                print(f"[ERROR] Incident {incident_id} failed: {error}")

            await asyncio.sleep(SLEEP_BETWEEN_CALLS)


if __name__ == "__main__":
    asyncio.run(main())
