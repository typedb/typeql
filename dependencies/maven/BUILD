# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.

exports_files(["artifacts.bzl"])

load("@typedb_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    exclude = ["artifacts.snapshot"],
    license_type = "mpl-header",
)
