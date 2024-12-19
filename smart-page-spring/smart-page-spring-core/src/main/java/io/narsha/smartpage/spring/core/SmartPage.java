package io.narsha.smartpage.spring.core;

import io.narsha.smartpage.core.QueryExecutor;
import io.narsha.smartpage.core.SmartPageQuery;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

/**
 * Smart Page entrypoint
 *
 * @param <P> kind of dto
 */
public abstract class SmartPage<P> extends AbstractSmartPage<P> {

  private final QueryExecutor<P> executor;

  /**
   * constructor
   *
   * @param executor executor
   */
  public SmartPage(QueryExecutor<P> executor) {
    this.executor = executor;
  }

  /**
   * generate a spring response entity which contains the response body and some http header RFC988
   * more information https://datatracker.ietf.org/doc/html/rfc5988#page-6
   *
   * @param query the query to execute
   * @param <T> targeted DTO type
   * @return the http response entity
   */
  public <T> ResponseEntity<List<T>> asResponseEntity(SmartPageQuery<T> query) {
    return asResponseEntity(query, null);
  }

  /**
   * generate a spring response entity which contains the response body and some http header RFC988
   * more information https://datatracker.ietf.org/doc/html/rfc5988#page-6
   *
   * @param query the query to execute
   * @param extraParameters use this if you want to apply some extra parameters to your query
   * @param <T> targeted DTO type
   * @return the http response entity
   */
  public <T> ResponseEntity<List<T>> asResponseEntity(SmartPageQuery<T> query, P extraParameters) {
    var result = executor.execute(query, extraParameters);
    if (CollectionUtils.isEmpty(result.data())) {
      return ResponseEntity.noContent().build();
    }

    final var headers = generatePaginationHeaders(query.page(), query.size(), result);
    return ResponseEntity.ok().headers(headers).body(result.data());
  }
}
