# MyBatis Generator Plugin

## 概要

MyBatis Generatorのカスタムプラグインを開発するリポジトリです。

MyBatis Generatorについては以下のリンクを参照してください。

- [公式サイト](https://mybatis.org/generator/)
- [GitHub](https://github.com/mybatis/generator)

## プラグインについて

### com.github.dkurata38.mybatis_generator_plugins.kotlin.NotNullFieldPlugin

このプラグインはtargetRuntimeに**Mybatis3Kotlin**を指定しているときだけ有効です。
テーブルのカラムのうち、NotNull制約のあるカラムに対応するRecordクラスのフィールドをNonNull型として生成します。


**プラグイン適用前**

```kotlin
data class ExampleTable(var exampleNonNull: String? = null,
                         var exampleNullable: String? = null) {
}
```

**プラグイン適用後**

```kotlin
data class ExampleTable(val exampleNonNull: String,
                        var exampleNullable: String? = null) {
}
```

### com.github.dkurata38.mybatis_generator_plugins.SeparateSubPackagesPlugin

このプラグインはMyBatisGeneratorで生成されるファイルをテーブルごとのサブパッケージに整理するプラグインです。targetPackageで指定したパッケージに対して、さらにテーブル名をもとにしたサブパッケージを作ります。

**プラグイン適用前**

+ target_package
    + ExampleTable
    + ExampleTableMapper
    + etc...
    
**プラグイン適用後**

+ target_package
    + example_table
        + ExampleTable
        + ExampleTableMapper
        + etc...
