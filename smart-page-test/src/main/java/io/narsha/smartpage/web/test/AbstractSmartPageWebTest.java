package io.narsha.smartpage.web.test;

import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.params.provider.Arguments;

/** Global multi stack test class */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractSmartPageWebTest {

  /**
   * Global multi stack test source method
   *
   * @return result
   */
  protected static Stream<Arguments> webSource() {
    return Stream.of(
        Arguments.of(
            "allData",
            "/api/sales",
            null,
            4,
            """
                                [{
                                                            "itemId":1,
                                                            "itemName":"T-SHIRT",
                                                            "storeId":1,
                                                            "storeName":"PARIS",
                                                            "quantity":2
                                                          },{
                                                            "itemId":1,
                                                            "itemName":"T-SHIRT",
                                                            "storeId":2,
                                                            "storeName":"SEOUL",
                                                            "quantity":1
                                                          },{
                                                            "itemId":1,
                                                            "itemName":"T-SHIRT",
                                                            "storeId":3,
                                                            "storeName":"BEIJING",
                                                            "quantity":1
                                                          },{
                                                            "itemId":3,
                                                            "itemName":"CAP",
                                                            "storeId":2,
                                                            "storeName":"SEOUL",
                                                            "quantity":1
                                                          }]
                                                        """),
        Arguments.of(
            "paginatedDataFirstPage",
            "/api/sales?page=0&size=2",
            "<http://localhost/api/sales?page=1&size=2>; rel=\"next\",<http://localhost/api/sales?page=1&size=2>; rel=\"last\"",
            4,
            """
                                [{
                                    "itemId":1,
                                    "itemName":"T-SHIRT",
                                    "storeId":1,
                                    "storeName":"PARIS",
                                    "quantity":2
                                  },{
                                    "itemId":1,
                                    "itemName":"T-SHIRT",
                                    "storeId":2,
                                    "storeName":"SEOUL",
                                    "quantity":1
                                  }]
                                """),
        Arguments.of(
            "paginatedDataSecondPage",
            "/api/sales?page=1&size=2",
            "<http://localhost/api/sales?page=0&size=2>; rel=\"first\",<http://localhost/api/sales?page=0&size=2>; rel=\"prev\"",
            4,
            """
                                [{
                                    "itemId":1,
                                    "itemName":"T-SHIRT",
                                    "storeId":3,
                                    "storeName":"BEIJING",
                                    "quantity":1
                                  },{
                                    "itemId":3,
                                    "itemName":"CAP",
                                    "storeId":2,
                                    "storeName":"SEOUL",
                                    "quantity":1
                                  }]
                                """),
        Arguments.of(
            "testSortAsc",
            "/api/sales?sort=itemId,asc",
            null,
            4,
            """
                                [{
                                    "itemId":1,
                                    "itemName":"T-SHIRT",
                                    "storeId":1,
                                    "storeName":"PARIS",
                                    "quantity":2
                                  },{
                                    "itemId":1,
                                    "itemName":"T-SHIRT",
                                    "storeId":2,
                                    "storeName":"SEOUL",
                                    "quantity":1
                                  },{
                                    "itemId":1,
                                    "itemName":"T-SHIRT",
                                    "storeId":3,
                                    "storeName":"BEIJING",
                                    "quantity":1
                                  },{
                                    "itemId":3,
                                    "itemName":"CAP",
                                    "storeId":2,
                                    "storeName":"SEOUL",
                                    "quantity":1
                                  }]
                                """),
        Arguments.of(
            "testSortDesc",
            "/api/sales?sort=storeId,desc",
            null,
            4,
            """
                                [{
                                    "itemId":1,
                                    "itemName":"T-SHIRT",
                                    "storeId":3,
                                    "storeName":"BEIJING",
                                    "quantity":1
                                 },{
                                    "itemId":1,
                                    "itemName":"T-SHIRT",
                                    "storeId":2,
                                    "storeName":"SEOUL",
                                    "quantity":1
                                  },{
                                    "itemId":3,
                                    "itemName":"CAP",
                                    "storeId":2,
                                    "storeName":"SEOUL",
                                    "quantity":1
                                  },{
                                    "itemId":1,
                                    "itemName":"T-SHIRT",
                                    "storeId":1,
                                    "storeName":"PARIS",
                                    "quantity":2
                                  }
                                ]
                                """),
        Arguments.of(
            "testSortWithRename",
            "/api/sales?sort=quantity,desc",
            null,
            4,
            """
                                 [{
                                 "itemId":1,
                                 "itemName":"T-SHIRT",
                                 "storeId":1,
                                 "storeName":"PARIS",
                                 "quantity":2
                                 },{
                                 "itemId":1,
                                 "itemName":"T-SHIRT",
                                 "storeId":2,
                                 "storeName":"SEOUL",
                                 "quantity":1
                                },{
                                "itemId":1,
                                "itemName":"T-SHIRT",
                                "storeId":3,
                                "storeName":"BEIJING",
                                "quantity":1
                                },{
                                "itemId":3,
                                "itemName":"CAP",
                                "storeId":2,
                                "storeName":"SEOUL",
                                "quantity":1
                                }
                                 ]
                                 """),
        Arguments.of(
            "testFilterWithRename",
            "/api/sales?quantity=2",
            null,
            1,
            """
                                [{
                                    "itemId":1,
                                    "itemName":"T-SHIRT",
                                    "storeId":1,
                                    "storeName":"PARIS",
                                    "quantity":2
                                  }
                                  ]"""),
        Arguments.of(
            "testFilterWithFilterType",
            "/api/sales?quantity=2&filter=quantity,equals",
            null,
            1,
            """
                                [{
                                    "itemId":1,
                                    "itemName":"T-SHIRT",
                                    "storeId":1,
                                    "storeName":"PARIS",
                                    "quantity":2
                                  }
                                  ]"""),
        Arguments.of(
            "testFilterWithLessThanFilterType",
            "/api/sales?quantity=2&filter=quantity,lt",
            null,
            "3",
            """
                                [{"itemId":1,"itemName":"T-SHIRT","storeId":2,"storeName":"SEOUL","quantity":1},{"itemId":1,"itemName":"T-SHIRT","storeId":3,"storeName":"BEIJING","quantity":1},{"itemId":3,"itemName":"CAP","storeId":2,"storeName":"SEOUL","quantity":1}]"""),
        Arguments.of(
            "testFilterWithLessThanOrEqualsFilterType",
            "/api/sales?quantity=1&filter=quantity,lte",
            null,
            3,
            """
                                [{"itemId":1,"itemName":"T-SHIRT","storeId":2,"storeName":"SEOUL","quantity":1},{"itemId":1,"itemName":"T-SHIRT","storeId":3,"storeName":"BEIJING","quantity":1},{"itemId":3,"itemName":"CAP","storeId":2,"storeName":"SEOUL","quantity":1}]
                                                           """),
        Arguments.of(
            "testFilterWithGreaterThanFilterType",
            "/api/sales?quantity=1&filter=quantity,gt",
            null,
            1,
            """
                                [{"itemId":1,"itemName":"T-SHIRT","storeId":1,"storeName":"PARIS","quantity":2}]"""),
        Arguments.of(
            "testFilterWithGreaterThanOrEqualsFilterType",
            "/api/sales?quantity=2&filter=quantity,gte",
            null,
            1,
            """
                                [{"itemId":1,"itemName":"T-SHIRT","storeId":1,"storeName":"PARIS","quantity":2}]
                                                             """),
        Arguments.of(
            "testFilterWithContainsFilterType",
            "/api/sales?itemName=IR&filter=itemName,contains",
            null,
            3,
            """
                                [{"itemId":1,"itemName":"T-SHIRT","storeId":1,"storeName":"PARIS","quantity":2},{"itemId":1,"itemName":"T-SHIRT","storeId":2,"storeName":"SEOUL","quantity":1},{"itemId":1,"itemName":"T-SHIRT","storeId":3,"storeName":"BEIJING","quantity":1}]
                                                             """),
        Arguments.of(
            "testFilterWithContainsFilterType",
            "/api/sales?itemId=1,2&filter=itemId,in",
            null,
            3,
            """
                                        [{"itemId":1,"itemName":"T-SHIRT","storeId":1,"storeName":"PARIS","quantity":2},{"itemId":1,"itemName":"T-SHIRT","storeId":2,"storeName":"SEOUL","quantity":1},{"itemId":1,"itemName":"T-SHIRT","storeId":3,"storeName":"BEIJING","quantity":1}]
                                                                     """));
  }
}
