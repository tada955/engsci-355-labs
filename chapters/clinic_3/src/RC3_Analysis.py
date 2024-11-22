import pandas as pd
import numpy as np, scipy.stats as st

naStrings = ["this.SimTime/1[h]", "Scenario", "Replication",  "this.obj",
  "Event", "EventTime"]

lab_data = pd.read_table("RC3-PatientEventLogger.log", header=0, names=[
  "LeaveTime", "Scenario", "Replication", "Object", "Event", "EventTime"
], skiprows=15, na_values=naStrings).dropna()

lab_data = lab_data.astype({"LeaveTime": np.float64, "Scenario": str,
  "Replication": np.float64, "Object": str, "Event": str,
  "EventTime": np.float64})

lab_data['Batch'] = np.ceil(lab_data['LeaveTime'] / (24 * 7))
scenario_indices = lab_data['Scenario'].str.split('-', expand=True)
lab_data['CTMachineScenario'] = scenario_indices.loc[:, 0]
lab_data['ReceptionistScenario'] = scenario_indices.loc[:, 1]

check_in_waits = lab_data[lab_data['Event'] == "WaitForCheckIn"]
check_in_waits['CheckInWaitTime'] = lab_data.query('Event == "CheckIn"')['EventTime'].values - check_in_waits['EventTime'].values

check_in_90 = check_in_waits.groupby(['CTMachineScenario', 'ReceptionistScenario', 'Batch'])['CheckInWaitTime'].quantile(0.90).reset_index()

means = check_in_90.groupby(['CTMachineScenario', 'ReceptionistScenario']).mean()

# check_in_waits["CheckInWaitTime"] = 

TIS_90 = lab_data.groupby(['Replication'])['TimeInSystem'].quantile(0.90).reset_index()

conf_int = TIS_90.agg(
  Mean=("TimeInSystem", "mean"),
  CI_Half_Width=("TimeInSystem", lambda x: x.sem() * st.norm.ppf(0.975)),
)

print(conf_int)
