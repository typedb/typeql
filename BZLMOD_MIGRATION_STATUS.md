# TypeQL: Bzlmod Migration Status

**Status: COMPLETE**

All 20 targets build successfully with Bazel 8.0.0 using Bzlmod.

## Build Verification

```bash
bazelisk build //...
```

**Results:**
- 20 targets analyzed
- All targets build successfully
- No exclusions required

## Targets

| Target | Status |
|--------|--------|
| `//rust:typeql` | ✅ Rust library |
| `//rust:typeql_unit_tests` | ✅ Unit tests |
| `//rust:assemble_crate` | ✅ Crate assembly |
| `//rust:deploy_crate` | ✅ Crate deployment |
| `//rust:deploy_github` | ✅ GitHub deployment |
| `//rust:rustfmt_test` | ✅ Format check |
| `//:deploy-github` | ✅ Root GitHub deployment |
| `//:tools` | ✅ Development tools |
| Checkstyle tests | ✅ All pass |

## Configuration

### MODULE.bazel

Key configurations:
- **Rust**: 1.81.0 with isolated crate extension
- **Python**: 3.11 toolchain
- **Kotlin**: toolchain for checkstyle/tools
- **Local overrides**: `typedb_dependencies`, `typedb_bazel_distribution`, `typedb_behaviour`

### Experimental Flags

`.bazelrc` includes:
```
common --experimental_isolated_extension_usages
```

This is required to avoid crate universe conflicts between typeql and typedb_dependencies.

## Dependencies

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
└── Rust crates (from typedb_dependencies)
    ├── pest (2.8.0)
    ├── pest_derive (2.8.0)
    ├── regex (1.11.1)
    ├── chrono (0.4.40)
    └── itertools (0.10.5)
```

## Migration Notes

1. **Crate Extension Isolation**: Uses `isolate = True` to avoid conflicts with typedb_dependencies' crate extension.

2. **Rust Analyzer Toolchain**: The `@rust_analyzer_toolchain_tools` reference was removed from the tools filegroup as it's not automatically created by the Bzlmod extension. IDE support can be configured separately if needed.

3. **Shared Crates**: TypeQL uses the shared crate registry from `@typedb_dependencies//:library/crates/`, not its own Cargo.toml.

## Files

| File | Purpose |
|------|---------|
| `MODULE.bazel` | Bzlmod configuration |
| `MODULE.bazel.lock` | Dependency lock file |
| `.bazelversion` | Bazel 8.0.0 |
| `.bazelrc` | Build flags including experimental flags |
| `WORKSPACE` | Deprecated, kept for backward compatibility |

## Consumers

TypeQL is consumed by:
- `typedb` - Core database server
- `typedb-driver` - Client drivers
- `typedb-console` - CLI client
- `typedb-studio` - GUI client
