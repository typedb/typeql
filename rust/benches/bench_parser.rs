/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{fs::File, os::raw::c_int, path::Path};

use criterion::{Criterion, criterion_group, criterion_main, profiler::Profiler};
use pprof::ProfilerGuard;

const N_LINES: usize = 1000;
fn get_query_isa() -> String {
    let mut query: String = "match\n".to_owned();
    (0..N_LINES).for_each(|_| query.push_str("$v_abc123 isa t_abc123 == 123456;\n"));
    query
}

fn get_query_var_sub() -> String {
    let mut query: String = "match\n".to_owned();
    (0..N_LINES).for_each(|_| query.push_str("$v_abc123 sub t_abc123;\n"));
    query
}

fn get_query_label_sub() -> String {
    let mut query: String = "match\n".to_owned();
    (0..N_LINES).for_each(|_| query.push_str("t_pqr456 sub t_abc123;\n"));
    query
}

fn get_query_expression() -> String {
    let mut query: String = "match\n".to_owned();
    (0..N_LINES).for_each(|_| query.push_str("let $volume = (4/3) * 3.14 * pow($r, 3) ;\n"));
    query
}

fn get_query_disjunctions() -> String {
    let stmt = "$v_abc123 isa t_abc123 == 123456;\n";
    let line = format!("{{ {stmt} }} or\n");

    let mut query: String = "match\n".to_owned();
    (0..(N_LINES - 1)).for_each(|_| query.push_str(line.as_str()));
    query.push_str(format!("{{ {stmt} }};").as_str());
    query
}

fn get_query_many_inserts() -> String {
    let mut query: String = "".to_owned();
    (0..N_LINES).for_each(|_| query.push_str("insert $_ isa person;\n"));
    query
}

fn get_define_label_sub() -> String {
    let mut query: String = "define\n".to_owned();
    (0..N_LINES).for_each(|_| query.push_str("t_pqr456 sub t_abc123;\n"));
    query
}

fn criterion_benchmark(c: &mut Criterion) {
    println!("In criterion benchmark");
    let query_isa = get_query_isa();
    let query_label_sub = get_query_isa();
    let query_var_sub = get_query_var_sub();
    let query_expression = get_query_expression();
    let query_disjunctions = get_query_disjunctions();
    let query_many_inserts = get_query_many_inserts();
    let define_label_sub = get_define_label_sub();
    c.bench_function("parse-isa", |b| b.iter(|| typeql::parse_query(query_isa.as_str()).unwrap()));
    c.bench_function("parse-var-sub", |b| b.iter(|| typeql::parse_query(query_var_sub.as_str()).unwrap()));
    c.bench_function("parse-label-sub", |b| b.iter(|| typeql::parse_query(query_label_sub.as_str()).unwrap()));
    c.bench_function("parse-expression", |b| b.iter(|| typeql::parse_query(query_expression.as_str()).unwrap()));
    c.bench_function("parse-disjunctions", |b| b.iter(|| typeql::parse_query(query_disjunctions.as_str()).unwrap()));
    c.bench_function("parse-many-inserts", |b| b.iter(|| typeql::parse_query(query_many_inserts.as_str()).unwrap()));
    c.bench_function("parse-define-sub", |b| b.iter(|| typeql::parse_query(define_label_sub.as_str()).unwrap()));
}
// --- Code to generate flamegraphs copied from https://www.jibbow.com/posts/criterion-flamegraphs/ ---
// This causes a SIGBUS on (mac) arm64 if the frequency is set too high.

pub struct FlamegraphProfiler<'a> {
    frequency: c_int,
    active_profiler: Option<ProfilerGuard<'a>>,
}

impl<'a> FlamegraphProfiler<'a> {
    #[allow(dead_code)]
    pub fn new(frequency: c_int) -> Self {
        FlamegraphProfiler { frequency, active_profiler: None }
    }
}

impl<'a> Profiler for FlamegraphProfiler<'a> {
    fn start_profiling(&mut self, _benchmark_id: &str, _benchmark_dir: &Path) {
        self.active_profiler = Some(ProfilerGuard::new(self.frequency).unwrap());
    }

    fn stop_profiling(&mut self, _benchmark_id: &str, benchmark_dir: &Path) {
        std::fs::create_dir_all(benchmark_dir).unwrap();
        let flamegraph_path = benchmark_dir.join("flamegraph.svg");
        let flamegraph_file = File::create(flamegraph_path).expect("File system error while creating flamegraph.svg");
        if let Some(profiler) = self.active_profiler.take() {
            profiler.report().build().unwrap().flamegraph(flamegraph_file).expect("Error writing flamegraph");
        }
    }
}

fn profiled() -> Criterion {
    Criterion::default().with_profiler(FlamegraphProfiler::new(100))
}

criterion_group!(
    name = benches;
    config = profiled();
    targets = criterion_benchmark
);

criterion_main!(benches);
