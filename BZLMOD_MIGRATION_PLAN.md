# TypeQL: Bazel 6 to 8 Migration Plan

## Overview

Migrate `typeql` from WORKSPACE-based builds to Bzlmod (MODULE.bazel) for Bazel 8 compatibility.

**Current Status:** Not started - uses Bazel 6.2.0 with WORKSPACE

---

## Current State

| Aspect | Status |
|--------|--------|
| Bazel Version | 6.2.0 |
| MODULE.bazel | Does not exist |
| WORKSPACE | Active, primary dependency source |
| Build Status | Working on Bazel 6 |

### Key Dependencies (from WORKSPACE)

- `@typedb_dependencies` - Build tooling and shared dependencies
- `@typedb_behaviour` - BDD test specifications
- `@rules_rust` - Rust build rules
- `@rules_antlr` - ANTLR parser rules
- `@rules_python` - Python build rules
- `@rules_jvm_external` - Maven dependencies
- `@io_bazel_rules_kotlin` - Kotlin build rules
- `@crates` - Rust crate dependencies
- `@typedb_bazel_distribution` - Deployment rules

### Main Targets

- `//rust:typeql` - Rust library (primary deliverable)
- `//rust:typeql_unit_tests` - Unit tests
- `//rust:assemble_crate` - crates.io package assembly
- `//rust:deploy_crate` - crates.io deployment
- `:deploy_github` - GitHub release deployment
- `:checkstyle` - Code style validation

---

## Migration Steps

### Step 1: Create MODULE.bazel

Create `/opt/project/repositories/typeql/MODULE.bazel` with:

```python
module(
    name = "typeql",
    version = "0.0.0",
    compatibility_level = 1,
)

# Core BCR dependencies
bazel_dep(name = "bazel_skylib", version = "1.7.1")
bazel_dep(name = "rules_python", version = "1.0.0")
bazel_dep(name = "rules_rust", version = "0.56.0")
bazel_dep(name = "rules_kotlin", version = "2.0.0")
bazel_dep(name = "rules_jvm_external", version = "6.6")
bazel_dep(name = "rules_pkg", version = "1.0.1")

# typedb_dependencies via local path override
bazel_dep(name = "typedb_dependencies", version = "0.0.0")
local_path_override(
    module_name = "typedb_dependencies",
    path = "../dependencies",
)

# typedb_bazel_distribution (transitive via typedb_dependencies)
bazel_dep(name = "typedb_bazel_distribution", version = "0.0.0")
local_path_override(
    module_name = "typedb_bazel_distribution",
    path = "../bazel-distribution",
)

# typedb_behaviour for BDD tests
bazel_dep(name = "typedb_behaviour", version = "0.0.0")
local_path_override(
    module_name = "typedb_behaviour",
    path = "../typedb-behaviour",
)

# Python toolchain
python = use_extension("@rules_python//python/extensions:python.bzl", "python")
python.toolchain(
    is_default = True,
    python_version = "3.11",
)

# Rust toolchain
rust = use_extension("@rules_rust//rust:extensions.bzl", "rust")
rust.toolchain(
    edition = "2021",
    versions = ["1.81.0"],
)
use_repo(rust, "rust_toolchains")
register_toolchains("@rust_toolchains//:all")

# Rust crates - use isolated extension to avoid conflicts with typedb_dependencies
crate = use_extension("@rules_rust//crate_universe:extensions.bzl", "crate", isolate = True)
crate.from_cargo(
    name = "crates",
    cargo_lockfile = "//:Cargo.lock",
    manifests = ["//:Cargo.toml"],
)
use_repo(crate, "crates")

# Register Kotlin toolchain
register_toolchains("@rules_kotlin//kotlin/internal:default_toolchain")
```

### Step 2: Update .bazelversion

Change from `6.2.0` to `8.0.0`.

### Step 3: Update .bazelrc

Add Bzlmod configuration:

```
# Bzlmod is now the default (Bazel 7+)
# WORKSPACE is kept for backward compatibility but is deprecated

# Enable isolated extension usages for crate universe
common --experimental_isolated_extension_usages
```

### Step 4: Verify All Targets Build

```bash
bazelisk build //...
```

Expected targets:
- `//rust:typeql` - Rust library
- `//rust:typeql_unit_tests` - Unit tests
- `//rust:assemble_crate` - Crate assembly
- `//rust:deploy_crate` - Crate deployment
- `:deploy_github` - GitHub deployment
- Checkstyle tests

### Step 5: Handle Potential Issues

**Issue 1: Crate universe conflict**
- Both typeql and typedb_dependencies define crate extensions
- Solution: Use `isolate = True` and `--experimental_isolated_extension_usages`

**Issue 2: ANTLR rules**
- TypeQL uses ANTLR for parsing (via typedb_dependencies)
- Verify ANTLR rules are properly exposed from typedb_dependencies

**Issue 3: Maven dependencies**
- typedb_dependencies has version_conflict_policy = "pinned"
- Should resolve transitive dependency conflicts

### Step 6: Documentation

Create `BZLMOD_MIGRATION_STATUS.md` with:
- Build verification command
- Target status
- Known issues (if any)

---

## Dependencies Graph

```
typeql
├── typedb_dependencies (local)
│   └── typedb_bazel_distribution (local, transitive)
├── typedb_behaviour (local, for BDD tests)
├── BCR modules
│   ├── rules_rust (0.56.0)
│   ├── rules_python (1.0.0)
│   ├── rules_kotlin (2.0.0)
│   ├── rules_jvm_external (6.6)
│   └── rules_pkg (1.0.1)
└── Rust crates (from Cargo.lock)
    ├── pest (2.8.0)
    ├── regex (1.11.1)
    ├── chrono (0.4.40)
    └── itertools (0.10.5)
```

---

## Verification Commands

```bash
# Full build
cd /opt/project/repositories/typeql
bazelisk build //...

# Test Rust library specifically
bazelisk build //rust:typeql

# Run tests
bazelisk test //rust:typeql_unit_tests

# Verify crate assembly
bazelisk build //rust:assemble_crate
```

---

## Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| Crate universe conflict | High | Medium | Use isolate=True |
| ANTLR rule issues | Low | Medium | ANTLR loaded via typedb_dependencies |
| Maven conflicts | Low | Low | Already fixed in typedb_dependencies |
| BDD test failures | Low | Medium | typedb_behaviour already migrated |

---

## Files to Modify

| File | Action |
|------|--------|
| `MODULE.bazel` | Create new |
| `.bazelversion` | Update 6.2.0 → 8.0.0 |
| `.bazelrc` | Add Bzlmod config |
| `WORKSPACE` | Keep for backward compatibility (deprecated) |
| `BZLMOD_MIGRATION_STATUS.md` | Create after migration |

---

## Rollback Plan

If migration fails, re-enable WORKSPACE mode:
```bash
# Add to .bazelrc
common --enable_workspace=true
common --noenable_bzlmod
```
