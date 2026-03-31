// TODO: Move to benches?
use std::time::{Duration, Instant};

const N_LINES: usize = 1000;
const N_ITERS: usize = 1000;

fn do_timing(query: &str, n_iters: usize) -> Duration {
    // Warm up
    crate::parse_query(query).unwrap();

    let start = Instant::now();
    for _ in 0..n_iters {
        crate::parse_query(query).unwrap();
    }
    let end = Instant::now();
    end - start
}

// #[ignore]
#[test]
fn time_isa() {
    let mut query: String = "match\n".to_owned();
    (0..N_LINES).for_each(|_| query.push_str("$v_abc123 isa t_abc123 == 123456;\n"));
    let duration = do_timing(&query, N_ITERS);
    println!(
        "Completed 'time_isa' in {}s. iters={}; lines={}; total_len = {}",
        duration.as_secs_f64(),
        N_ITERS,
        N_LINES,
        query.len()
    );
}

// #[ignore]
#[test]
fn time_var_sub() {
    let mut query: String = "match\n".to_owned();
    (0..N_LINES).for_each(|_| query.push_str("$v_abc123 sub t_abc123;\n"));
    let duration = do_timing(&query, N_ITERS);
    println!(
        "Completed 'time_var_sub' in {}s. iters={}; lines={}; total_len = {}",
        duration.as_secs_f64(),
        N_ITERS,
        N_LINES,
        query.len()
    );
}

// #[ignore]
#[test]
fn time_label_sub() {
    let mut query: String = "match\n".to_owned();
    (0..N_LINES).for_each(|_| query.push_str("t_pqr456 sub t_abc123;\n"));
    let duration = do_timing(&query, N_ITERS);
    println!(
        "Completed 'time_label_sub' in {}s. iters={}; lines={}; total_len = {}",
        duration.as_secs_f64(),
        N_ITERS,
        N_LINES,
        query.len()
    );
}

// #[ignore]
#[test]
fn time_or() {
    let stmt = "$v_abc123 isa t_abc123 == 123456;\n";
    let line = format!("{{ {stmt} }} or\n");

    let mut query: String = "match\n".to_owned();
    (0..(N_LINES - 1)).for_each(|_| query.push_str(line.as_str()));
    query.push_str(format!("{{ {stmt} }};").as_str());

    let duration = do_timing(&query, N_ITERS);
    println!(
        "Completed 'time_or' in {}s. iters={}; lines={}; total_len = {}",
        duration.as_secs_f64(),
        N_ITERS,
        N_LINES,
        query.len()
    );
}
