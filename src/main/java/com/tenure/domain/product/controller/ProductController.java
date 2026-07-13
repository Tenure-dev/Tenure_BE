package com.tenure.domain.product.controller;

import com.tenure.domain.product.dto.ProductCreateRequest;
import com.tenure.domain.product.dto.ProductCreateResponse;
import com.tenure.domain.product.dto.ProductDetailResponse;
import com.tenure.domain.product.dto.ProductUpdateRequest;
import com.tenure.domain.product.dto.ProductUpdateResponse;
import com.tenure.domain.product.service.ProductService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product", description = "판매 상품 API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "아이템 판매 전환",
            description = "보유 중인 아이템을 판매 상품으로 전환합니다. 기본 사용자는 수수료 SELLER_PAYS, 배송비 0원만 허용합니다.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "JWT 적용 전 Swagger 테스트용 현재 사용자 ID. JWT 적용 후에는 SecurityContext 값을 사용합니다.",
                            example = "1"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ProductCreateRequest.class),
                            examples = @ExampleObject(
                                    name = "판매 전환 요청",
                                    value = """
                                            {
                                              "price": 50000,
                                              "shippingFee": 0,
                                              "feePolicy": "SELLER_PAYS",
                                              "mainImageUrl": "https://image.url/product.jpg",
                                              "measurements": {
                                                "shoulder": 45,
                                                "chest": 55,
                                                "totalLength": 70
                                              },
                                              "conditionFlags": ["NO_DEFECT"],
                                              "sellerDescription": "3회 착용했습니다.",
                                              "attachedOotdIds": [1, 2]
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "판매 전환 성공",
            content = @Content(
                    schema = @Schema(implementation = ProductCreateResponse.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "success": true,
                                      "code": "COMMON_200",
                                      "message": "판매 상품이 생성되었습니다.",
                                      "data": {
                                        "productId": 1,
                                        "itemId": 10,
                                        "sellerUserId": 1,
                                        "price": 50000,
                                        "shippingFee": 0,
                                        "feePolicy": "SELLER_PAYS",
                                        "productStatus": "ON_SALE",
                                        "itemStatus": "ON_SALE",
                                        "attachedOotdIds": [1, 2]
                                      }
                                    }
                                    """
                    )
            )
    )
    @PostMapping("/items/{itemId}/product")
    public BaseResponse<ProductCreateResponse> createProduct(
            @PathVariable Long itemId,
            @Valid @RequestBody ProductCreateRequest request
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        ProductCreateResponse response = productService.createProduct(itemId, currentUserId, request);
        return BaseResponse.success(response, "판매 상품이 생성되었습니다.");
    }

    @Operation(
            summary = "판매 상품 상세 조회",
            description = "판매 상품 상세를 조회합니다. 판매자 비공개 계정의 미승인 사용자는 403 PRIVATE_403으로 차단합니다.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "JWT 적용 전 Swagger 테스트용 현재 사용자 ID. JWT 적용 후에는 SecurityContext 값을 사용합니다.",
                            example = "2"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "판매 상품 상세 조회 성공",
            content = @Content(
                    schema = @Schema(implementation = ProductDetailResponse.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "success": true,
                                      "code": "COMMON_200",
                                      "message": "조회에 성공했습니다.",
                                      "data": {
                                        "productId": 1,
                                        "viewerMode": "BUYER",
                                        "availableActions": ["CHAT", "PURCHASE", "SHARE", "REPORT"],
                                        "productStatus": "ON_SALE",
                                        "item": {
                                          "itemId": 10,
                                          "brandName": "Nike",
                                          "itemName": "Black Jacket",
                                          "sizeSystem": "KR",
                                          "sizeValue": "100",
                                          "categoryLarge": "아우터",
                                          "categorySmall": "블루종",
                                          "ootdVerifiedWearCount": 3,
                                          "wishCount": 12
                                        },
                                        "seller": {
                                          "userId": 1,
                                          "username": "YuJin",
                                          "profileImageUrl": "https://image.url/profile.jpg",
                                          "grade": "BASIC"
                                        },
                                        "price": 50000,
                                        "shippingFee": 0,
                                        "feePolicy": "SELLER_PAYS",
                                        "mainImageUrl": "https://image.url/product.jpg",
                                        "measurements": {
                                          "shoulder": 45,
                                          "chest": 55,
                                          "totalLength": 70
                                        },
                                        "conditionFlags": ["NO_DEFECT"],
                                        "sellerDescription": "3회 착용했습니다.",
                                        "representativeOotds": [
                                          {
                                            "ootdId": 1,
                                            "imageUrl": "https://image.url/ootd.jpg",
                                            "createdAt": "2026-07-10T12:00:00"
                                          }
                                        ]
                                      }
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(responseCode = "403", description = "비공개 계정 미승인 접근")
    @GetMapping("/products/{productId}")
    public BaseResponse<ProductDetailResponse> getProductDetail(@PathVariable Long productId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        ProductDetailResponse response = productService.getProductDetail(productId, currentUserId);
        return BaseResponse.success(response, "조회에 성공했습니다.");
    }

    @Operation(
            summary = "Update product",
            description = "The seller updates an ON_SALE product. attachedOotdIds must belong to the product item and the current seller.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Temporary current user id for Swagger/local testing before JWT is fully connected.",
                            example = "1"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ProductUpdateRequest.class),
                            examples = @ExampleObject(
                                    name = "Product update request",
                                    value = """
                                            {
                                              "price": 52000,
                                              "shippingFee": 0,
                                              "feePolicy": "SELLER_PAYS",
                                              "mainImageUrl": "https://image.url/product.jpg",
                                              "measurements": {
                                                "shoulder": 45,
                                                "chest": 55,
                                                "totalLength": 70
                                              },
                                              "conditionFlags": ["STAIN"],
                                              "sellerDescription": "상태 설명 수정",
                                              "attachedOotdIds": [1, 2]
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product updated successfully.",
            content = @Content(schema = @Schema(implementation = ProductUpdateResponse.class))
    )
    @PatchMapping("/products/{productId}")
    public BaseResponse<ProductUpdateResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request
    ) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        ProductUpdateResponse response = productService.updateProduct(productId, currentUserId, request);
        return BaseResponse.success(response, "판매 상품을 수정했습니다.");
    }
}
