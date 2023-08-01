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
                         var exampleNullable: String? = null) 
```

**プラグイン適用後**

```kotlin
data class ExampleTable(val exampleNonNull: String,
                        var exampleNullable: String? = null) 
```
