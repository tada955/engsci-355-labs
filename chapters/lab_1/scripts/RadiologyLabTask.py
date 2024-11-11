import pandas as pd
import numpy as np, scipy.stats as st

lab_data_task = pd.read_table("radiology_lab_task.dat", header=0, names=[
  'Scenario', 'Replication', 'TimeInSystem', 'Utilisation'
]).dropna()

conf_int = lab_data_task['Utilisation'].agg({
  'Mean': 'mean',
  'CI Half Width': lambda x: x.sem() * st.norm.ppf(0.975),
})

print(conf_int)
