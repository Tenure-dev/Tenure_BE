package com.tenure.domain.product.controller;

import com.tenure.domain.product.dto.ProductCreateRequest;
import com.tenure.domain.product.dto.ProductCreateResponse;
import com.tenure.domain.product.dto.ProductDeleteResponse;
import com.tenure.domain.product.dto.ProductDetailResponse;
import com.tenure.domain.product.dto.ProductExternalCompleteResponse;
import com.tenure.domain.product.dto.ProductUpdateRequest;
import com.tenure.domain.product.dto.ProductUpdateResponse;
import com.tenure.domain.product.service.ProductService;
import com.tenure.global.response.BaseResponse;
import com.tenure.global.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product", description = "Product API")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "Create product from item",
            description = "The owner converts an owned item into an ON_SALE product.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Temporary current user id for Swagger/local testing before JWT is fully connected.",
                            example = "1"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product created successfully.",
            content = @Content(schema = @Schema(implementation = ProductCreateResponse.class))
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
            summary = "Get product detail",
            description = "Returns product detail with viewer mode and available actions.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Temporary current user id for Swagger/local testing before JWT is fully connected.",
                            example = "2"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product detail returned successfully.",
            content = @Content(schema = @Schema(implementation = ProductDetailResponse.class))
    )
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
            }
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

    @Operation(
            summary = "Complete product externally",
            description = "The seller marks an ON_SALE product as sold outside Tenure. The product and item become SOLD, pending intents/offers are canceled, and no trade or item transfer is created.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Temporary current user id for Swagger/local testing before JWT is fully connected.",
                            example = "1"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product completed externally.",
            content = @Content(schema = @Schema(implementation = ProductExternalCompleteResponse.class))
    )
    @PostMapping("/products/{productId}/complete-external")
    public BaseResponse<ProductExternalCompleteResponse> completeExternalProduct(@PathVariable Long productId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        ProductExternalCompleteResponse response = productService.completeExternalProduct(productId, currentUserId);
        return BaseResponse.success(response, "외부 판매 완료로 변경했습니다.");
    }

    @Operation(
            summary = "Delete product posting",
            description = "The seller hides an ON_SALE product posting and reverts the item to OWNED. The product and item rows are not deleted.",
            parameters = {
                    @Parameter(
                            name = "X-USER-ID",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Temporary current user id for Swagger/local testing before JWT is fully connected.",
                            example = "1"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product posting deleted successfully.",
            content = @Content(schema = @Schema(implementation = ProductDeleteResponse.class))
    )
    @DeleteMapping("/products/{productId}")
    public BaseResponse<ProductDeleteResponse> deleteProduct(@PathVariable Long productId) {
        Long currentUserId = currentUserProvider.getCurrentUserId();
        ProductDeleteResponse response = productService.deleteProduct(productId, currentUserId);
        return BaseResponse.success(response, "판매 게시를 삭제했습니다.");
    }
}
