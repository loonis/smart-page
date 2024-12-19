package io.narsha.smartpage.spring.sql;

import io.narsha.smartpage.spring.core.ReactiveSmartPage;
import java.util.Map;

/** spring jdbc implementation */
public class SmartPageR2dbc extends ReactiveSmartPage<Map<String, Object>> {

  /**
   * constructor
   *
   * @param jdbcQueryExecutor jdbcQueryExecutor
   */
  public SmartPageR2dbc(R2dbcQueryExecutor jdbcQueryExecutor) {
    super(jdbcQueryExecutor);
  }
}
