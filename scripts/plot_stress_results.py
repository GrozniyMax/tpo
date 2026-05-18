#!/usr/bin/env python3
"""Plot stress test results for Config 3."""

import pandas as pd
import matplotlib.pyplot as plt

# Load data
data = []
for i in ['1', '1.3', '1.4', '1.5', '2', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15']:
    try:
        df = pd.read_csv(f'stress/{i}/table.csv')
        df = df[df['threadName'].str.contains('Config 3:')]
        df['load'] = df['allThreads'].max()
        data.append(df)
    except:
        pass

df = pd.concat(data)

# Aggregate by load
agg = df.groupby('load').agg(
    avg_rt=('elapsed', 'mean'),
    success=('success', lambda x: x.mean() * 100)
).reset_index().sort_values('load')

# Colors
colors = []
for _, r in agg.iterrows():
    if r['load'] == 410:
        colors.append('#f1c40f')  # Yellow for 410
    elif r['success'] == 100 and r['avg_rt'] <= 640:
        colors.append('#27ae60')  # Green - meets SLA
    else:
        colors.append('#e74c3c')  # Red - doesn't meet SLA

# Plot
fig, ax = plt.subplots(figsize=(12, 6))
ax.bar(range(len(agg)), agg['avg_rt'], color=colors, edgecolor='black', alpha=0.8)

for i, rt in enumerate(agg['avg_rt']):
    ax.text(i, rt + 300, f'{rt:.0f}', ha='center', va='bottom', fontsize=8, rotation=45)

ax.axhline(y=640, color='red', linestyle='--', linewidth=2.5, label='Макс. (640 мс)')
ax.set_xlabel('Нагрузка (пользователей)')
ax.set_ylabel('Среднее время отклика (мс)')
ax.set_title('Config 3: Время отклика vs Нагрузка')
ax.set_xticks(range(len(agg)))
ax.set_xticklabels([str(int(x)) for x in agg['load']], rotation=45, ha='right')
ax.legend(loc='upper left')
ax.grid(True, alpha=0.3, axis='y')
ax.set_axisbelow(True)

plt.tight_layout()
plt.savefig('graphs/response_time_vs_load_config_3.png', dpi=150, bbox_inches='tight')
plt.close()

print(f"Loaded {len(df)} requests")
print(f"Load levels: {sorted(agg['load'].astype(int).tolist())}")
print("Saved to: graphs/response_time_vs_load_config_3.png")
