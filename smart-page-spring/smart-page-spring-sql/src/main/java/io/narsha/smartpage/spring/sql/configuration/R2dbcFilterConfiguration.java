package io.narsha.smartpage.spring.sql.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.narsha.smartpage.core.RowMapper;
import io.narsha.smartpage.core.configuration.AbstractFilterConfiguration;
import io.narsha.smartpage.spring.sql.R2dbcQueryExecutor;
import io.narsha.smartpage.spring.sql.SmartPageR2dbc;
import io.narsha.smartpage.spring.sql.filters.JdbcFilter;
import io.narsha.smartpage.spring.sql.filters.JdbcFilterRegistrationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

/** Spring jdbc filter configuration */
@Configuration
// @ConditionalOnBean(DatabaseClient.class)
public class R2dbcFilterConfiguration
    extends AbstractFilterConfiguration<JdbcFilter, JdbcFilterRegistrationService> {

  /** default constructor */
  public R2dbcFilterConfiguration() {}

  /**
   * Auto-register all JdbcFilter implementations
   *
   * @return the populated jdbcFilterRegistrationService with all JdbcFilter in the same package as
   *     JdbcFilter
   * @throws Exception if reflection Exception
   */
  @Bean
  public JdbcFilterRegistrationService jdbcFilterRegistrationService() throws Exception {
    return super.init(JdbcFilter.class);
  }

  /**
   * Init a JdbcQueryExecutor that will be in charge of execute the final SQL query
   *
   * @param databaseClient execute the final sql query
   * @param jdbcFilterRegistrationService will generate sql clause from DTO
   * @param rowMapper will convert the sql data as DTO
   * @return JdbcQueryExecutor
   */
  @Bean
  public R2dbcQueryExecutor r2dbcQueryExecutor(
      DatabaseClient databaseClient,
      JdbcFilterRegistrationService jdbcFilterRegistrationService,
      RowMapper rowMapper) {
    return new R2dbcQueryExecutor(databaseClient, jdbcFilterRegistrationService, rowMapper);
  }

  /**
   * RowMapper that automatically map the query data into the targeted DTO
   *
   * @return rowMapper
   */
  @Bean
  public RowMapper rowMapper() {
    return new RowMapper(new ObjectMapper());
  }

  /**
   * Create the jdbc instance for smart page
   *
   * @param r2dbcQueryExecutor JdbcQueryExecutor that will be in charge of execute the final SQL
   *     query
   * @return smart page jdbc instance
   */
  @Bean
  public SmartPageR2dbc smartPageR2dbc(R2dbcQueryExecutor r2dbcQueryExecutor) {
    return new SmartPageR2dbc(r2dbcQueryExecutor);
  }
}
