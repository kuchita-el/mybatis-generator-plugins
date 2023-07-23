# MyBatis Generator Plugin

## 概要

MyBatis Generatorのカスタムプラグインを開発するリポジトリです。

MyBatis Generatorについては以下のリンクを参照してください。

- [公式サイト](https://mybatis.org/generator/)
- [GitHub](https://github.com/mybatis/generator)

## インストール方法

### Maven

Mavenプロジェクト/libディレクトリにプラグインを配置してください。

```xml
<plugin>
    <groupId>org.mybatis.generator</groupId>
    <artifactId>mybatis-generator-maven-plugin</artifactId>
    <version>x.x.x</version>
    <executions>
        <execution>
            <id>Mybatis Generator Artifacts</id>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>com.github.dkurata38</groupId>
            <artifactId>mybatis-generator-plugins-core</artifactId>
            <version>x.x.x</version>
            <scope>system</scope>
            <systemPath>${basedir}/lib/mybatis-generator-plugins-core-x.x.x.jar</systemPath>
        </dependency>
    </dependencies>
</plugin>
```

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

### com.github.dkurata38.mybatis_generator_plugins.mybatis3.CustomAliasedColumnListPlugin

このプラグインはMyBaitsGeneratorで出力されるXMLファイルに対して、includeする元でテーブルのエイリアスを指定できるようなカラムの一覧を追加で生成します。targetRuntimeがMyBatis3でmapperTypeがXMLMAPPER/MIXEDMAPPERであるときに有効です。

**追加で生成される要素の例**

```xml
<sql id="CustomAliasedBaseColumnList">
${alias}.example_column1 as ${alias}_example_column1,
${alias}.example_column2 as ${alias}_example_column2
</sql>
```

include元では以下のように使えます。

```xml
<select id="selectWithSubTableById">
    SELECT
        <include refid="Base_Column_List"/>
        , <include refid="target_package.SubTableMapper.CustomAliasedBaseColumnList">
            <property name="alias" value="st"/>
        </include>
    FROM
        sub_table st
        INNER JOIN main_table
            ON st.main_table_id = main_table.main_table_id
    WHERE
        main_table.main_table_id = #{id}
</select>

```

このとき`target_package.SubTableMapper.CustomAliasedBaseColumnList`をincludeしている要素は次のように展開されます。

```sql
SELECT
    st.example_column1 as st_example_column1,
    st.example_column2 as st_example_column2
FROM
    sub_table st
```

CustomAliasedBaseColumnListを使ったクエリの実行結果とBeanのマッピングでは、association要素やcollection要素のcolumnPrefix属性が便利です。

```xml
<resultMap id="WithSubTableMap" extends="BaseResultMap" type="target_package.MainTable">
    <association columnPrefix="st_" resultMap="target_package.SubTableMapper.BaseResultMap" property="subTable"/>
</resultMap>
```

generatorConfig.xmlでtable要素のalias属性を設定している場合は、CustomAliasedBaseColumnListのaliasにはtable要素で指定したaliasにprefixをつける形式で指定すると、baseResultMapを流用してマッピングをすることができます。

```xml
<!-- SubTableMapper.xml -->
<mapper>
    <resultMap id="BaseResultMap" type="target_package.SubTable">
        <id property="exampleColumn1" column="st_example_column1"/>
        <result property="exampleColumn2" column="st_example_column2"/>
    </resultMap>
</mapper>
```

```xml
<!-- MainTableMapper.xml -->
<mapper>
    <resultMap id="WithSubTableMap" extends="BaseResultMap" type="target_package.MainTable">
        <association columnPrefix="a_" resultMap="target_package.SubTableMapper.BaseResultMap" property="subTable"/>
    </resultMap>
    <select id="selectWithSubTableById">
        SELECT
            <include refid="Base_Column_List"/>
            , <include refid="target_package.SubTableMapper.CustomAliasedBaseColumnList">
                <property name="alias" value="a_st"/>
            </include>
        FROM
            sub_table a_st
            INNER JOIN main_table
                ON st.main_table_id = main_table.main_table_id
        WHERE
            main_table.main_table_id = #{id}
    </select>
</mapper>
```
