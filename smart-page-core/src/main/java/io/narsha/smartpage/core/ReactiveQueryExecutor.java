package io.narsha.smartpage.core;

import reactor.core.publisher.Mono;

/**
 * Interface that give a reactive query data for a paginatedfilterQuery
 *
 * @param <P> type of extra parameters
 */
public interface ReactiveQueryExecutor<P> {

  /**
   * Execute the query
   *
   * @param paginatedFilteredQuery query information needed to execute the query
   * @param extraParameters use this if you want to apply some extra parameters to your query
   * @param <T> type of the DTO data
   * @return a Mono of query data
   */
  <T> Mono<SmartPageResult<T>> execute(SmartPageQuery<T> paginatedFilteredQuery, P extraParameters);
}
