package io.narsha.smartpage.spring.sql.example;

import io.narsha.smartpage.core.SmartPageQuery;
import io.narsha.smartpage.spring.sql.SmartPageR2dbc;
import io.narsha.smartpage.spring.sql.example.dto.Sales;
import io.narsha.smartpage.spring.sql.example.dto.SalesWithFilteredQuery;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/** Simple RestController that expose an endpoint to get filtered data */
@RestController
@RequestMapping("/api/sales")
public class SalesController {

  private final SmartPageR2dbc smartPage;

  /**
   * constructor
   *
   * @param smartPageR2dbc smartPage service
   */
  public SalesController(SmartPageR2dbc smartPageR2dbc) {
    this.smartPage = smartPageR2dbc;
  }

  /**
   * list sales
   *
   * @param query filter to apply
   * @return filtered data
   */
  @GetMapping
  public Mono<ResponseEntity<List<Sales>>> sales(SmartPageQuery<Sales> query) {
    return smartPage.asResponseEntity(query);
  }

  /**
   * list sales using custom filter
   *
   * @param query filter to apply
   * @return filtered data
   */
  @GetMapping("/custom-filter")
  public Mono<ResponseEntity<List<SalesWithFilteredQuery>>> salesFilteredByCurrentUser(
      SmartPageQuery<SalesWithFilteredQuery> query) {
    final var currentUserStore = "SEOUL"; // SecurityContext.getCurrentUserStore();
    return smartPage.asResponseEntity(query, Map.of("storeName", currentUserStore));
  }

  // Lambda function to create a self link
  private static final Function<Object, Link> createSelfSalesLink =
      (Object id) ->
          WebMvcLinkBuilder.linkTo(
                  WebMvcLinkBuilder.methodOn(SalesController.class).getSales((Long) id))
              .withSelfRel();

  //  // Lambda function to create next/previous page link
  //  private static final Function<Object, Link> createAllSalesNextPageLink = (Object query) ->
  //
  // WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(SalesController.class).getAllSales((SmartPageQuery<Sales>) query)).withRel("next");

  @GetMapping("/custom2")
  public Mono<PagedModel<EntityModel<Sales>>> getAllSales(SmartPageQuery<Sales> query) {
    return smartPage.asHateoasPageModel(query, createSelfSalesLink);
  }

  @GetMapping("/{id}")
  public EntityModel<Sales> getSales(@PathVariable("id") Long id) {
    // TODO
    return null;
  }
}
