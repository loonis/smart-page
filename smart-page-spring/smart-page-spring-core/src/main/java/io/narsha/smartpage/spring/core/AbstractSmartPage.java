package io.narsha.smartpage.spring.core;

import static io.narsha.smartpage.core.utils.HeaderUtils.X_TOTAL_COUNT;

import io.narsha.smartpage.core.SmartPageResult;
import io.narsha.smartpage.core.utils.HeaderUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Smart Page entrypoint
 *
 * @param <P> kind of dto
 */
public abstract class AbstractSmartPage<P> {

  protected <T> HttpHeaders generatePaginationHeaders(
      Integer page, Integer size, SmartPageResult<T> result) {
    final var headers = new HttpHeaders();

    var link =
        HeaderUtils.generateHeader(
            ServletUriComponentsBuilder.fromCurrentRequest().toUriString(), page, size, result);

    if (StringUtils.isNotBlank(link)) {
      headers.set(HttpHeaders.LINK, link);
    }
    headers.set(X_TOTAL_COUNT, String.valueOf(result.total()));
    return headers;
  }
}
