#!/usr/bin/env python3
"""
Plot throughput graphs for 3 software configurations from JMeter load test data.
Throughput is calculated based on response time (1/elapsed) - showing actual
processing capacity of each configuration.
"""

import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.dates as mdates
from pathlib import Path


def load_config_data(load_dir: Path) -> dict[str, pd.DataFrame]:
    """Load CSV data for each configuration."""
    configs = {}
    config_files = {
        "Config 1": load_dir / "config1Table.csv",
        "Config 2": load_dir / "config2Table.csv",
        "Config 3": load_dir / "config3Table.csv",
    }

    for name, file_path in config_files.items():
        if file_path.exists():
            df = pd.read_csv(file_path)
            # Convert timestamp from milliseconds to datetime
            df["timestamp"] = pd.to_datetime(df["timeStamp"], unit="ms")
            # Calculate instantaneous throughput: requests per second = 1000 / elapsed_ms
            df["instant_throughput"] = 1000.0 / df["elapsed"]
            configs[name] = df
        else:
            print(f"Warning: File not found: {file_path}")

    return configs


def calculate_throughput(df: pd.DataFrame, window_seconds: int = 5) -> pd.DataFrame:
    """
    Calculate average throughput (requests per second) using a sliding window.
    Based on actual response times, not just request count.

    Args:
        df: DataFrame with timestamp and instant_throughput columns
        window_seconds: Size of the sliding window in seconds

    Returns:
        DataFrame with time intervals and throughput values
    """
    if df.empty:
        return pd.DataFrame()

    df = df.sort_values("timestamp").copy()
    df = df.set_index("timestamp")

    # Calculate mean throughput in each window
    throughput = df["instant_throughput"].resample(f"{window_seconds}s").mean()

    # Convert to DataFrame
    result = throughput.reset_index()
    result.columns = ["timestamp", "throughput"]

    return result


def plot_throughput_comparison(configs: dict[str, pd.DataFrame], output_dir: Path) -> None:
    """Create and save throughput comparison plot."""
    plt.style.use("seaborn-v0_8-whitegrid")

    fig, ax = plt.subplots(figsize=(14, 7))

    colors = {
        "Config 1": "#e74c3c",  # Red
        "Config 2": "#f39c12",  # Orange
        "Config 3": "#27ae60",  # Green
    }

    window_seconds = 5

    for config_name, df in configs.items():
        throughput_df = calculate_throughput(df, window_seconds)

        if not throughput_df.empty:
            ax.plot(
                throughput_df["timestamp"],
                throughput_df["throughput"],
                label=config_name,
                color=colors.get(config_name, "blue"),
                linewidth=2.5,
                marker="o",
                markersize=6,
            )

    ax.set_xlabel("Время", fontsize=12)
    ax.set_ylabel("Пропускная способность (requests/second)", fontsize=12)
    ax.set_title("Пропускная способность по конфигурации", fontsize=14, fontweight="bold")

    ax.legend(loc="best", fontsize=11)

    # Format x-axis dates
    ax.xaxis.set_major_formatter(mdates.DateFormatter("%H:%M:%S"))
    plt.xticks(rotation=45)

    # Add grid
    ax.grid(True, alpha=0.3)

    plt.tight_layout()

    # Save plot
    output_file = output_dir / "throughput_comparison.png"
    plt.savefig(output_file, dpi=150, bbox_inches="tight")
    print(f"Plot saved to: {output_file}")

    plt.close()


def plot_response_time_comparison(configs: dict[str, pd.DataFrame], output_dir: Path) -> None:
    """Create and save response time comparison plot."""
    plt.style.use("seaborn-v0_8-whitegrid")

    fig, ax = plt.subplots(figsize=(14, 7))

    colors = {
        "Config 1": "#e74c3c",
        "Config 2": "#f39c12",
        "Config 3": "#27ae60",
    }

    for config_name, df in configs.items():
        ax.plot(
            df["timestamp"],
            df["elapsed"],
            label=config_name,
            color=colors.get(config_name, "blue"),
            linewidth=1.5,
            marker=".",
            markersize=4,
            alpha=0.7,
        )

    # Add maximum allowed response time threshold (640 ms)
    ax.axhline(y=640, color="red", linestyle="--", linewidth=2.5, 
               label="Max Allowed (640 ms)")

    ax.set_xlabel("Время", fontsize=12)
    ax.set_ylabel("Время отклика(мс)", fontsize=12)
    ax.set_title("Сравнение времени отклика", fontsize=14, fontweight="bold")

    ax.legend(loc="best", fontsize=11)

    ax.xaxis.set_major_formatter(mdates.DateFormatter("%H:%M:%S"))
    plt.xticks(rotation=45)

    ax.grid(True, alpha=0.3)

    plt.tight_layout()

    output_file = output_dir / "response_time_comparison.png"
    plt.savefig(output_file, dpi=150, bbox_inches="tight")
    print(f"Plot saved to: {output_file}")

    plt.close()


def plot_individual_throughput(configs: dict[str, pd.DataFrame], output_dir: Path) -> None:
    """Create individual throughput plots for each configuration."""
    window_seconds = 5

    for config_name, df in configs.items():
        fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(12, 8), sharex=True)

        throughput_df = calculate_throughput(df, window_seconds)

        if not throughput_df.empty:
            # Throughput plot
            ax1.fill_between(
                throughput_df["timestamp"],
                throughput_df["throughput"],
                alpha=0.3,
            )
            ax1.plot(
                throughput_df["timestamp"],
                throughput_df["throughput"],
                linewidth=2,
                color="#27ae60",
            )

            avg_throughput = throughput_df["throughput"].mean()
            max_throughput = throughput_df["throughput"].max()
            min_throughput = throughput_df["throughput"].min()

            ax1.axhline(y=avg_throughput, color="red", linestyle="--", linewidth=1.5,
                       label=f"Average: {avg_throughput:.2f} req/s")

            ax1.set_ylabel("Пропускная способность (req/s)", fontsize=11)
            ax1.set_title(f"{config_name} - Пропускная способность и время отклика", fontsize=13, fontweight="bold")
            ax1.legend(loc="best", fontsize=10)
            ax1.grid(True, alpha=0.3)

            # Response time plot
            ax2.plot(df["timestamp"], df["elapsed"], linewidth=1, alpha=0.6, color="#e74c3c")
            ax2.axhline(y=df["elapsed"].mean(), color="red", linestyle="--", linewidth=1.5,
                       label=f"Avg: {df['elapsed'].mean():.0f} ms")
            ax2.set_xlabel("Время", fontsize=11)
            ax2.set_ylabel("Время ответа (ms)", fontsize=11)
            ax2.legend(loc="best", fontsize=10)
            ax2.grid(True, alpha=0.3)

            ax1.xaxis.set_major_formatter(mdates.DateFormatter("%H:%M:%S"))
            plt.xticks(rotation=45)

            # Add maximum allowed response time threshold (640 ms)
            ax2.axhline(y=640, color="red", linestyle="--", linewidth=2, 
                       label="Максимально допустимое время обработки запроса (640 ms)")

            plt.tight_layout()

            safe_name = config_name.replace(" ", "_").lower()
            output_file = output_dir / f"throughput_{safe_name}.png"
            plt.savefig(output_file, dpi=150, bbox_inches="tight")
            print(f"Individual plot saved to: {output_file}")

        plt.close()


def print_statistics(configs: dict[str, pd.DataFrame]) -> None:
    """Print throughput and response time statistics for each configuration."""
    print("\n" + "=" * 70)
    print("PERFORMANCE STATISTICS")
    print("=" * 70)

    window_seconds = 5

    for config_name, df in configs.items():
        throughput_df = calculate_throughput(df, window_seconds)

        if not throughput_df.empty:
            avg_throughput = throughput_df["throughput"].mean()
            max_throughput = throughput_df["throughput"].max()
            min_throughput = throughput_df["throughput"].min()

            avg_response = df["elapsed"].mean()
            min_response = df["elapsed"].min()
            max_response = df["elapsed"].max()
            p95_response = df["elapsed"].quantile(0.95)
            total_requests = len(df)
            success_rate = df["success"].mean() * 100

            print(f"\n{config_name}:")
            print(f"  Total Requests:     {total_requests}")
            print(f"  Success Rate:       {success_rate:.1f}%")
            print()
            print(f"  Throughput (req/s):")
            print(f"    Average: {avg_throughput:.2f}")
            print(f"    Min:     {min_throughput:.2f}")
            print(f"    Max:     {max_throughput:.2f}")
            print()
            print(f"  Response Time (ms):")
            print(f"    Average: {avg_response:.0f}")
            print(f"    Min:     {min_response:.0f}")
            print(f"    Max:     {max_response:.0f}")
            print(f"    P95:     {p95_response:.0f}")

    print("\n" + "=" * 70)


def main():
    # Define paths
    script_dir = Path(__file__).parent
    project_root = script_dir.parent
    load_dir = project_root / "load"
    output_dir = project_root / "graphs"

    # Create output directory
    output_dir.mkdir(parents=True, exist_ok=True)

    # Load data
    print("Loading configuration data...")
    configs = load_config_data(load_dir)

    if not configs:
        print("Error: No configuration data found!")
        return

    print(f"Loaded {len(configs)} configuration(s)")

    # Print statistics
    print_statistics(configs)

    # Create plots
    print("\nGenerating graphs...")
    plot_throughput_comparison(configs, output_dir)
    plot_response_time_comparison(configs, output_dir)
    plot_individual_throughput(configs, output_dir)

    print("\nDone!")


if __name__ == "__main__":
    main()
