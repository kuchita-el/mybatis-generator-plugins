package com.github.kuchita_el.mybatis_generator_plugins.system_test.select_for_update

import com.github.kuchita_el.mybatis_generator_plugins.system_test.utils.getConnection
import com.github.kuchita_el.mybatis_generator_plugins.system_test.utils.openSqlSession
import com.github.kuchita_el.mybatis_generator_plugins.system_test.utils.selectOne
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.sql.Connection
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import com.github.kuchita_el.mybatis_generator_plugins.system_test.select_for_update.mybatis3.annotated.MemberMapper as Mybatis3AnnotatedMemberMapper
import com.github.kuchita_el.mybatis_generator_plugins.system_test.select_for_update.mybatis3.mixed.MemberMapper as Mybatis3MixedMemberMapper
import com.github.kuchita_el.mybatis_generator_plugins.system_test.select_for_update.mybatis3.xml.MemberMapper as Mybatis3XmlMemberMapper
import com.github.kuchita_el.mybatis_generator_plugins.system_test.select_for_update.mybatis3_simple.annotated.MemberMapper as MyBatis3SimpleAnnotatedMemberMapper
import com.github.kuchita_el.mybatis_generator_plugins.system_test.select_for_update.mybatis3_simple.xml.MemberMapper as Mybatis3SimpleXmlMemberMapper

class SelectForUpdatePluginTest {
    @Nested
    inner class MyBatis3AnnotatedTest {
        @Test
        fun トランザクション内でselectForUpdateを呼び出したら行ロックを獲得すること() {
            val connection =
                getConnection(postgres.driverClassName, postgres.jdbcUrl, postgres.username, postgres.password)

            openSqlSession(postgres.driverClassName, postgres.jdbcUrl, postgres.username, postgres.password)
                .use { session ->
                    val memberMapper = session.getMapper(Mybatis3AnnotatedMemberMapper::class.java)
                    val member = memberMapper.selectByPrimaryKeyForUpdate(memberId)

                    assertAll(
                        { assertEquals(memberId, member.memberId) },
                        { assertEquals(name, member.name) },
                    )

                    val numberOfLockedRows = countRowShareLocks(connection, "member")
                    assertEquals(1, numberOfLockedRows.getOrThrow())
                    session.rollback()
                }

            val numberOfLockedRows = countRowShareLocks(connection, "member")
            assertEquals(0, numberOfLockedRows.getOrThrow())

            connection.close()
        }
    }

    @Nested
    inner class MyBatis3MixedTest {
        @Test
        fun トランザクション内でselectForUpdateを呼び出したら行ロックを獲得すること() {
            val connection =
                getConnection(postgres.driverClassName, postgres.jdbcUrl, postgres.username, postgres.password)

            openSqlSession(postgres.driverClassName, postgres.jdbcUrl, postgres.username, postgres.password)
                .use { session ->
                    val memberMapper = session.getMapper(Mybatis3MixedMemberMapper::class.java)
                    val member = memberMapper.selectByPrimaryKeyForUpdate(memberId)

                    assertAll(
                        { assertEquals(memberId, member.memberId) },
                        { assertEquals(name, member.name) },
                    )

                    val numberOfLockedRows = countRowShareLocks(connection, "member")
                    assertEquals(1, numberOfLockedRows.getOrThrow())
                    session.rollback()
                }

            val numberOfLockedRows = countRowShareLocks(connection, "member")
            assertEquals(0, numberOfLockedRows.getOrThrow())

            connection.close()
        }
    }

    @Nested
    inner class MyBatis3XMLTest {
        @Test
        fun トランザクション内でselectForUpdateを呼び出したら行ロックを獲得すること() {
            val connection =
                getConnection(postgres.driverClassName, postgres.jdbcUrl, postgres.username, postgres.password)

            openSqlSession(postgres.driverClassName, postgres.jdbcUrl, postgres.username, postgres.password)
                .use { session ->
                    val memberMapper = session.getMapper(Mybatis3XmlMemberMapper::class.java)
                    val member = memberMapper.selectByPrimaryKeyForUpdate(memberId)

                    assertAll(
                        { assertEquals(memberId, member.memberId) },
                        { assertEquals(name, member.name) },
                    )

                    val numberOfLockedRows = countRowShareLocks(connection, "member")
                    assertEquals(1, numberOfLockedRows.getOrThrow())
                    session.rollback()
                }

            val numberOfLockedRows = countRowShareLocks(connection, "member")
            assertEquals(0, numberOfLockedRows.getOrThrow())

            connection.close()
        }
    }

    @Nested
    inner class MyBatis3SimpleAnnotatedTest {
        @Test
        fun トランザクション内でselectForUpdateを呼び出したら行ロックを獲得すること() {
            val connection =
                getConnection(postgres.driverClassName, postgres.jdbcUrl, postgres.username, postgres.password)

            openSqlSession(postgres.driverClassName, postgres.jdbcUrl, postgres.username, postgres.password)
                .use { session ->
                    val memberMapper = session.getMapper(MyBatis3SimpleAnnotatedMemberMapper::class.java)
                    val member = memberMapper.selectByPrimaryKeyForUpdate(memberId)

                    assertAll(
                        { assertEquals(memberId, member.memberId) },
                        { assertEquals(name, member.name) },
                    )

                    val numberOfLockedRows = countRowShareLocks(connection, "member")
                    assertEquals(1, numberOfLockedRows.getOrThrow())
                    session.rollback()
                }

            val numberOfLockedRows = countRowShareLocks(connection, "member")
            assertEquals(0, numberOfLockedRows.getOrThrow())

            connection.close()
        }
    }

    @Nested
    inner class MyBatis3SimpleXMLTest {
        @Test
        fun トランザクション内でselectForUpdateを呼び出したら行ロックを獲得すること() {
            val connection =
                getConnection(postgres.driverClassName, postgres.jdbcUrl, postgres.username, postgres.password)

            openSqlSession(postgres.driverClassName, postgres.jdbcUrl, postgres.username, postgres.password)
                .use { session ->
                    val memberMapper = session.getMapper(Mybatis3SimpleXmlMemberMapper::class.java)
                    val member = memberMapper.selectByPrimaryKeyForUpdate(memberId)

                    assertAll(
                        { assertEquals(memberId, member.memberId) },
                        { assertEquals(name, member.name) },
                    )

                    val numberOfLockedRows = countRowShareLocks(connection, "member")
                    assertEquals(1, numberOfLockedRows.getOrThrow())
                    session.rollback()
                }

            val numberOfLockedRows = countRowShareLocks(connection, "member")
            assertEquals(0, numberOfLockedRows.getOrThrow())

            connection.close()
        }
    }

    companion object {
        private val memberId = "testMemberId"
        private val name = "testName"
        private val createdAt = LocalDateTime.now()
        private val updatedAt = LocalDateTime.now()

        val postgres: PostgreSQLContainer<*> =
            PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine")).apply {
                withInitScripts("sql/table.sql")
                start()

                createConnection("").use { connection ->
                    insertTestData(connection)
                }
            }

        @JvmStatic
        fun insertTestData(connection: Connection) {
            connection.prepareStatement(
                """
                insert into 
                    member(member_id, name, created_at, updated_at)
                values
                    (?, ?, ?, ?)""".trimIndent()
            ).use {
                it.setString(1, memberId)
                it.setString(2, name)
                it.setTimestamp(3, Timestamp.from(createdAt.atZone(ZoneId.systemDefault()).toInstant()))
                it.setTimestamp(4, Timestamp.from(updatedAt.atZone(ZoneId.systemDefault()).toInstant()))
                it.executeUpdate()
            }
        }

        @JvmStatic
        fun countRowShareLocks(
            connection: Connection,
            tableName: String,
        ): Result<Int?> {
            connection
                .prepareStatement(
                    """
                    select
                            count(*)
                        from
                            pg_locks
                            left outer join pg_class on pg_locks.relation = pg_class.oid
                        where
                            pg_locks.mode = 'RowShareLock'
                            and pg_class.relname = ?
                    """.trimIndent(),
                ).use {
                    it.setString(1, tableName)
                    return selectOne(it, { resultSet -> resultSet.getInt(1) })
                }
        }
    }
}
