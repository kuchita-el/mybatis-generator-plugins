package com.github.kuchita_el.mybatis_generator_plugins.type

enum class JavaClientGeneratorConfigurationType(
    val value: String,
) {
    XmlMapper("XMLMAPPER"),
    MixedMapper("MIXEDMAPPER"),
    AnnotatedMapper("ANNOTATEDMAPPER"),
}
