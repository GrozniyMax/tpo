#!/usr/bin/env python3
"""
Plot response time vs. load for a selected configuration from stress test data.
Shows how response time changes as the number of concurrent users increases.

Usage:
    python3 plot_response_time_vs_load.py --config 3
    python3 plot_response_time_vs_load.py --config 3 --output my_graph.png
"""

import argparse
import pandas as pd
import matplotlib.pyplot as plt
from pathlib import Path
from typing import Optional


def load_stress_data(stress_dir: Path, config: int) -> pd.DataFrame:
    """
    Load stress test data for a specific configuration from all experiment directories.
    Uses the maximum concurrent threads (allThreads) as the load level.
    
    Args:
        stress_dir: Path to stress test directory
        config: Configuration number (1, 2, or 3)
    
    Returns:
        DataFrame with load (threads) and response time data
    """
    all_data = []
    
    # Find all experiment directories (1, 1.3, 1.4, 1.5, 2, 3, ...)
    exp_dirs = sorted(
        [d for d in stress_dir.iterdir() if d.is_dir() and d.name.replace('.', '').isdigit()],
        key=lambda x: float(x.name)
    )
    
    print(f"Found {len(exp_dirs)} experiment directories")
    
    for exp_dir in exp_dirs:
        table_file = exp_dir / "table.csv"
        
        if not table_file.exists():
            print(f"  Skipping {exp_dir.name}: no table.csv")
            continue
        
        try:
            df = pd.read_csv(table_file)
            
            # Skip empty files
            if df.empty:
                print(f"  Skipping {exp_dir.name}: empty file")
                continue
            
            # Filter by configuration using threadName column (contains "Config X: Exp...")
            if 'threadName' in df.columns:
                config_pattern = f"Config {config}:"
                df = df[df['threadName'].str.contains(config_pattern, na=False)]
            
            if df.empty:
                print(f"  Skipping {exp_dir.name}: no data for Config {config}")
                continue
            
            # Use max allThreads as the load level (concurrent users)
            if 'allThreads' in df.columns:
                max_threads = df['allThreads'].max()
                df['load'] = max_threads
            else:
                # Fallback to experiment number
                df['load'] = float(exp_dir.name)
            
            # Extract response time
            if 'elapsed' in df.columns:
                df['response_time'] = df['elapsed']
            
            all_data.append(df[['load', 'response_time', 'success']])
            
            print(f"  Loaded Exp {exp_dir.name}: {len(df)} requests, load={df['load'].iloc[0]} users")
            
        except Exception as e:
            print(f"  Error loading {exp_dir.name}: {e}")
    
    if not all_data:
        return pd.DataFrame()
    
    return pd.concat(all_data, ignore_index=True)


def aggregate_by_load(df: pd.DataFrame) -> pd.DataFrame:
    """
    Aggregate response times by load level.
    
    Returns:
        DataFrame with avg, min, max, p95 response times per load level
    """
    if df.empty:
        return pd.DataFrame()
    
    agg = df.groupby('load').agg(
        avg_response_time=('response_time', 'mean'),
        min_response_time=('response_time', 'min'),
        max_response_time=('response_time', 'max'),
        p95_response_time=('response_time', lambda x: x.quantile(0.95)),
        std_response_time=('response_time', 'std'),
        request_count=('response_time', 'count'),
        success_rate=('success', lambda x: x.mean() * 100)
    ).reset_index()
    
    agg = agg.sort_values('load')
    return agg


def plot_response_time_vs_load(
    agg_df: pd.DataFrame, 
    output_path: Path,
    config: int,
    show_threshold: bool = True,
    threshold_ms: int = 640
) -> None:
    """
    Create bar chart showing average response time for each load level.
    
    Args:
        agg_df: Aggregated data by load level
        output_path: Path to save the plot
        config: Configuration number for title
        show_threshold: Whether to show max allowed response time threshold
        threshold_ms: Maximum allowed response time in ms
    """
    plt.style.use("seaborn-v0_8-whitegrid")
    
    fig, ax = plt.subplots(figsize=(14, 7))
    
    loads = agg_df['load'].astype(int).values
    avg_times = agg_df['avg_response_time'].values
    
    # Color bars based on whether they meet SLA
    colors = []
    for idx, row in agg_df.iterrows():
        if row['success_rate'] == 100 and row['avg_response_time'] <= threshold_ms:
            colors.append('#27ae60')  # Green - meets SLA
        else:
            colors.append('#e74c3c')  # Red - doesn't meet SLA
    
    # Create bar chart
    bars = ax.bar(
        range(len(loads)),
        avg_times,
        color=colors,
        edgecolor='black',
        linewidth=0.5,
        alpha=0.8,
        label='Average Response Time'
    )
    
    # Add value labels on top of bars
    for i, (load, time) in enumerate(zip(loads, avg_times)):
        ax.text(
            i, 
            time + 200, 
            f'{time:.0f}', 
            ha='center', 
            va='bottom', 
            fontsize=9,
            rotation=45
        )
    
    # Add threshold line
    if show_threshold:
        ax.axhline(
            y=threshold_ms, 
            color='red', 
            linestyle='--', 
            linewidth=2.5,
            label=f'Max Allowed ({threshold_ms} ms)'
        )
    
    ax.set_xlabel('Load (Concurrent Users)', fontsize=12)
    ax.set_ylabel('Average Response Time (ms)', fontsize=12)
    ax.set_title(
        f'Config {config}: Average Response Time by Load\n(Stress Test Results)',
        fontsize=14, 
        fontweight='bold'
    )
    
    ax.set_xticks(range(len(loads)))
    ax.set_xticklabels([str(x) for x in loads], rotation=45, ha='right')
    
    ax.legend(loc='upper left', fontsize=11)
    ax.grid(True, alpha=0.3, axis='y')
    ax.set_axisbelow(True)
    
    plt.tight_layout()
    
    plt.savefig(output_path, dpi=150, bbox_inches='tight')
    print(f"\nPlot saved to: {output_path}")
    
    plt.close()


def plot_success_rate_vs_load(
    agg_df: pd.DataFrame,
    output_path: Path,
    config: int
) -> None:
    """Create success rate vs. load plot."""
    plt.style.use("seaborn-v0_8-whitegrid")
    
    fig, ax = plt.subplots(figsize=(10, 6))
    
    ax.bar(
        agg_df['load'],
        agg_df['success_rate'],
        color='#3498db',
        alpha=0.7,
        edgecolor='black',
        linewidth=1
    )
    
    # Add 100% line
    ax.axhline(y=100, color='green', linestyle='--', linewidth=2, label='100% Success')
    
    # Add 0% line
    ax.axhline(y=0, color='red', linestyle='-', linewidth=1)
    
    ax.set_xlabel('Load (Concurrent Users)', fontsize=12)
    ax.set_ylabel('Success Rate (%)', fontsize=12)
    ax.set_title(f'Config {config}: Success Rate vs. Load', fontsize=14, fontweight='bold')
    
    ax.set_ylim(-5, 105)
    ax.set_xticks(agg_df['load'])
    ax.set_xticklabels([str(int(x)) for x in agg_df['load']])
    
    ax.legend(loc='lower left', fontsize=11)
    ax.grid(True, alpha=0.3, axis='y')
    
    plt.tight_layout()
    
    plt.savefig(output_path, dpi=150, bbox_inches='tight')
    print(f"Success rate plot saved to: {output_path}")
    
    plt.close()


def print_analysis(agg_df: pd.DataFrame, config: int) -> None:
    """Print analysis of the stress test results."""
    print("\n" + "=" * 70)
    print(f"STRESS TEST ANALYSIS - CONFIG {config}")
    print("=" * 70)
    
    if agg_df.empty:
        print("No data available for analysis")
        return
    
    print(f"\nLoad Levels Tested: {list(agg_df['load'].astype(int))}")
    print(f"Total Requests Analyzed: {agg_df['request_count'].sum()}")
    
    # Find breaking point (where success rate drops below 100% or response time exceeds threshold)
    threshold = 640
    breaking_point = None
    
    for _, row in agg_df.iterrows():
        if row['success_rate'] < 100 or row['avg_response_time'] > threshold:
            breaking_point = row['load']
            break
    
    print(f"\n{'Load':<8} {'Avg RT (ms)':<14} {'P95 RT (ms)':<14} {'Success %':<12} {'Requests':<10}")
    print("-" * 60)
    
    for _, row in agg_df.iterrows():
        status = "✓" if row['success_rate'] == 100 and row['avg_response_time'] <= threshold else "✗"
        print(f"{int(row['load']):<8} {row['avg_response_time']:<14.0f} {row['p95_response_time']:<14.0f} {row['success_rate']:<12.1f} {int(row['request_count']):<10} {status}")
    
    print("-" * 60)
    
    # Summary
    print(f"\nSUMMARY:")
    max_safe_load = agg_df[
        (agg_df['success_rate'] == 100) & 
        (agg_df['avg_response_time'] <= threshold)
    ]['load'].max()
    
    if pd.notna(max_safe_load):
        print(f"  Maximum load with 100% success and RT ≤ {threshold}ms: {int(max_safe_load)} users")
    else:
        print(f"  No load level meets SLA (100% success, RT ≤ {threshold}ms)")
    
    if breaking_point is not None:
        print(f"  System degradation starts at: {int(breaking_point)} users")
    
    print(f"\n  Best Response Time: {agg_df['avg_response_time'].min():.0f} ms (at {int(agg_df.loc[agg_df['avg_response_time'].idxmin(), 'load'])} users)")
    print(f"  Worst Response Time: {agg_df['avg_response_time'].max():.0f} ms (at {int(agg_df.loc[agg_df['avg_response_time'].idxmax(), 'load'])} users)")
    
    print("\n" + "=" * 70)


def main():
    parser = argparse.ArgumentParser(
        description='Plot response time vs. load for stress test data'
    )
    parser.add_argument(
        '--config', 
        type=int, 
        required=True,
        help='Configuration number (1, 2, or 3)'
    )
    parser.add_argument(
        '--output',
        type=str,
        default=None,
        help='Output file path (default: graphs/response_time_vs_load_config_X.png)'
    )
    parser.add_argument(
        '--no-threshold',
        action='store_true',
        help='Do not show the 640ms threshold line'
    )
    parser.add_argument(
        '--threshold',
        type=int,
        default=640,
        help='Maximum allowed response time in ms (default: 640)'
    )
    
    args = parser.parse_args()
    
    # Define paths
    script_dir = Path(__file__).parent
    project_root = script_dir.parent
    stress_dir = project_root / "stress"
    
    # Determine output path
    if args.output:
        output_path = Path(args.output)
    else:
        graphs_dir = project_root / "graphs"
        graphs_dir.mkdir(exist_ok=True)
        output_path = graphs_dir / f"response_time_vs_load_config_{args.config}.png"
    
    print(f"Loading stress test data for Config {args.config}...")
    print(f"Stress directory: {stress_dir}")
    
    # Load data
    df = load_stress_data(stress_dir, args.config)
    
    if df.empty:
        print(f"\nError: No data found for Config {args.config}")
        return
    
    print(f"\nTotal records loaded: {len(df)}")
    
    # Aggregate by load
    agg_df = aggregate_by_load(df)
    
    if agg_df.empty:
        print("\nError: Could not aggregate data")
        return
    
    # Print analysis
    print_analysis(agg_df, args.config)
    
    # Create plots
    print("\nGenerating plots...")
    plot_response_time_vs_load(
        agg_df, 
        output_path, 
        args.config,
        show_threshold=not args.no_threshold,
        threshold_ms=args.threshold
    )
    
    print("\nDone!")


if __name__ == "__main__":
    main()
