package io.narsha.smartpage.spring.sql;

import io.narsha.smartpage.core.PropertyFilter;
import io.narsha.smartpage.core.QueryExecutor;
import io.narsha.smartpage.core.RowMapper;
import io.narsha.smartpage.core.SmartPageQuery;
import io.narsha.smartpage.core.SmartPageResult;
import io.narsha.smartpage.core.utils.ResolverUtils;
import io.narsha.smartpage.spring.sql.filters.JdbcFilterRegistrationService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/** In charge of the sql query execution */
public class JdbcQueryExecutor implements QueryExecutor<Map<String, Object>> {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final JdbcFilterRegistrationService jdbcFilterRegistrationService;
  private final RowMapper rowMapper;

  /**
   * constructor
   *
   * @param jdbcTemplate jdbcTemplate
   * @param jdbcFilterRegistrationService jdbcFilterRegistrationService
   * @param rowMapper rowMapper
   */
  public JdbcQueryExecutor(
      NamedParameterJdbcTemplate jdbcTemplate,
      JdbcFilterRegistrationService jdbcFilterRegistrationService,
      RowMapper rowMapper) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcFilterRegistrationService = jdbcFilterRegistrationService;
    this.rowMapper = rowMapper;
  }

  @Override
  public <T> SmartPageResult<T> execute(
      SmartPageQuery<T> paginatedFilteredQuery, Map<String, Object> extraParameters) {
    final var jdbcQueryParser =
        new SqlQueryParser<>(paginatedFilteredQuery, jdbcFilterRegistrationService);

    jdbcQueryParser.init();

    final var params =
        paginatedFilteredQuery.filters().stream()
            .collect(Collectors.toMap(PropertyFilter::dataSourceProperty, PropertyFilter::value));

    if (extraParameters != null) {
      params.putAll(extraParameters);
    }

    final var data =
        this.jdbcTemplate.query(
            jdbcQueryParser.getQuery(),
            params,
            rs -> {
              return extractResultSet(paginatedFilteredQuery, rowMapper, rs);
            });

    final var count =
        this.jdbcTemplate.query(
            jdbcQueryParser.getCountQuery(),
            params,
            rs -> {
              rs.next();
              return rs.getInt(1);
            });

    return new SmartPageResult<>(data, count);
  }

  private <T> List<T> extractResultSet(
      SmartPageQuery<T> paginatedFilteredQuery, RowMapper rowMapper, ResultSet rs)
      throws SQLException {
    final var queryDefinition = getQueryDefinition(rs);

    final var result = new ArrayList<T>();
    while (rs.next()) {
      final var object = new HashMap<String, Object>();

      for (var entry : queryDefinition.entrySet()) {
        var javaProperty =
            ResolverUtils.getJavaProperty(paginatedFilteredQuery.targetClass(), entry.getKey());
        if (javaProperty.isPresent()) {
          object.put(javaProperty.get(), rs.getObject(entry.getValue()));
        }
      }
      result.add(rowMapper.convert(object, paginatedFilteredQuery.targetClass()));
    }
    return result;
  }

  private Map<String, Integer> getQueryDefinition(ResultSet resultSet) throws SQLException {
    final var resultSetMetaData = resultSet.getMetaData();
    final var columnCount = resultSetMetaData.getColumnCount();

    final var columns = new HashMap<String, Integer>();
    for (int i = 1; i <= columnCount; i++) {
      final var label = resultSetMetaData.getColumnLabel(i);
      columns.put(label, i);
    }

    return columns;
  }
}
