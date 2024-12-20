package io.narsha.smartpage.spring.core;

import io.narsha.smartpage.core.EntityWithId;
import io.narsha.smartpage.core.ReactiveQueryExecutor;
import io.narsha.smartpage.core.SmartPageQuery;
import io.narsha.smartpage.core.SmartPageResult;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

/**
 * Smart Page entrypoint
 *
 * @param <P> kind of dto
 */
public abstract class ReactiveSmartPage<P> extends AbstractSmartPage<P> {

  private final ReactiveQueryExecutor<P> executor;

  /**
   * constructor
   *
   * @param executor executor
   */
  public ReactiveSmartPage(ReactiveQueryExecutor<P> executor) {
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
  public <T> Mono<ResponseEntity<List<T>>> asResponseEntity(SmartPageQuery<T> query) {
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
  public <T> Mono<ResponseEntity<List<T>>> asResponseEntity(
      SmartPageQuery<T> query, P extraParameters) {
    var result = executor.execute(query, extraParameters);

    return result.map(
        r -> {
          if (CollectionUtils.isEmpty(r.data())) {
            return ResponseEntity.noContent().build();
          }

          final var headers = generatePaginationHeaders(query.page(), query.size(), r);
          return ResponseEntity.ok().headers(headers).body(r.data());
        });
  }

  public <T> Mono<PagedModel<EntityModel<T>>> asHateoasPageModel(
      SmartPageQuery<T> query, Function<Object, Link> createSelfLink) {
    return asHateoasPageModel(query, null, createSelfLink);
  }

  public <T> Mono<PagedModel<EntityModel<T>>> asHateoasPageModel(
      SmartPageQuery<T> query, P extraParameters, Function<Object, Link> createSelfLink) {

    Mono<SmartPageResult<T>> resultMono = executor.execute(query, extraParameters);

    Mono<PagedModel<EntityModel<T>>> pagedModelMono =
        resultMono.map(
            result -> {
              if (CollectionUtils.isEmpty(result.data())) {
                return PagedModel.empty();
              }

              // Wrap each data in an EntityModel
              List<EntityModel<T>> collect =
                  result.data().stream()
                      .map(
                          entity -> {
                            if (createSelfLink != null
                                && entity instanceof EntityWithId entityWithId) {
                              return EntityModel.of(
                                  entity, createSelfLink.apply(entityWithId.getId()));
                            }

                            return EntityModel.of(entity);
                          })
                      .collect(Collectors.toList());

              // Add pagination links
              long pageSize = collect.size();
              long pageNumber = query.page();
              long totalElements = result.total();
              long totalPage = totalElements / pageSize;
              PagedModel.PageMetadata metadata =
                  new PagedModel.PageMetadata(pageSize, pageNumber, result.total(), totalPage);
              PagedModel<EntityModel<T>> pagedModel = PagedModel.of(collect, metadata);

              //      if (pageNumber < totalPage - 1 && createNextPreviousLink!=null) {
              //        Integer nextPage = query.page() + 1;
              //        pagedModel.add(createNextPreviousLink.apply(copyQueryWithNewPage(query,
              // nextPage)).withRel("next"));
              //      }
              //
              //      if (pageNumber > 0 && createNextPreviousLink!=null) {
              //        Integer prevPage = query.page() - 1;
              //        pagedModel.add(createNextPreviousLink.apply(copyQueryWithNewPage(query,
              // prevPage)).withRel("prev"));
              //      }

              return pagedModel;
            });

    return pagedModelMono;
  }
}
