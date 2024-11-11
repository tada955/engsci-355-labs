import pandas as pd
import numpy as np, scipy.stats as st

lab_data = pd.read_table("radiology_lab.dat", header=0, names=[
  'Scenario', 'Replication', 'TimeInSystem'
]).dropna()

print(lab_data.head())

conf_int = lab_data['TimeInSystem'].agg({
  'Mean': 'mean',
  'CI Half Width': lambda x: x.sem() * st.norm.ppf(0.975),
})

print(conf_int)
