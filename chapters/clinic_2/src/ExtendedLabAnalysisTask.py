import pandas as pd
import numpy as np, scipy.stats as st

naStrings = ["this.SimTime/1[h]",  "this.obj", "[Simulation].RunNumber",
  "this.obj.TotalTime / 1[h]"]

lab_data = pd.read_table("RC2_task-PatientLogger.log", header=0, names=[
  "SimTime", "Object", "Replication", "TimeInSystem", "Priority"
], skiprows=14, na_values=naStrings).dropna()

lab_data = lab_data.astype({"SimTime": np.float64, "Object": str,
  "Replication": np.float64, "TimeInSystem": np.float64,
  "Priority": np.float64})

TIS_90 = lab_data.groupby(["Priority", 'Replication'])['TimeInSystem'].quantile(0.90).reset_index()

conf_int = TIS_90.groupby("Priority").agg(
  Mean=("TimeInSystem", "mean"),
  CI_Half_Width=("TimeInSystem", lambda x: x.sem() * st.norm.ppf(0.975)),
)

print(conf_int)
