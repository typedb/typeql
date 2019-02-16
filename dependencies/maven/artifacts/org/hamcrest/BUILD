java_library(
    name = "hamcrest-core",
    exports = [
        "//external:jar/org/hamcrest/hamcrest-core"
    ],
    visibility = [
        "//dependencies/maven/artifacts:__subpackages__"
    ]
)



java_library(
    name = "hamcrest-library",
    exports = [
        "//external:jar/org/hamcrest/hamcrest-library"
    ],
    runtime_deps = [
        ":hamcrest-core"
    ],
    visibility = [
        "//visibility:public"
    ]
)


