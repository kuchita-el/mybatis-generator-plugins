package com.github.kuchita_el.mybatis_generator_plugins.system_test.select_for_update

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface PgLocksMapper {

    @Select("""
    select
    count(*)
    from
    pg_locks
    left outer join pg_class on pg_locks.relation = pg_class.oid
    where
    pg_locks.mode = 'RowShareLock'
    and pg_class.relname = #{tableName}
    """)
    fun countRowShareLocksByTableName(tableName: String): Int
}
