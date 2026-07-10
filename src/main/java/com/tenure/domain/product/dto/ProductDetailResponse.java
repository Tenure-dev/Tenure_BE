package com.tenure.domain.product.dto;

import com.tenure.domain.common.enums.FeePolicy;
import com.tenure.domain.item.entity.Category;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.entity.ProductAttachedOotd;
import com.tenure.domain.product.enums.ProductAction;
import com.tenure.domain.product.enums.ProductStatus;
import com.tenure.domain.product.enums.ProductViewerMode;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.UserGrade;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "판매 상품 상세 응답")
public record ProductDetailResponse(

        @Schema(description = "판매 상품 ID", example = "1")
        Long productId,

        @Schema(description = "조회자 화면 모드", example = "BUYER")
        ProductViewerMode viewerMode,

        @ArraySchema(schema = @Schema(description = "조회자가 수행 가능한 액션", example = "PURCHASE"))
        List<ProductAction> availableActions,

        @Schema(description = "판매 상품 상태", example = "ON_SALE")
        ProductStatus productStatus,

        ItemSummary item,
        SellerSummary seller,

        @Schema(description = "판매 가격", example = "50000")
        Integer price,

        @Schema(description = "배송비. 0이면 판매자 부담", example = "0")
        Integer shippingFee,

        @Schema(description = "수수료 부담 방식", example = "SELLER_PAYS")
        FeePolicy feePolicy,

        @Schema(description = "대표 상품 이미지 URL", example = "https://image.url/product.jpg")
        String mainImageUrl,

        @Schema(description = "카테고리별 실측", example = "{\"shoulder\":45,\"chest\":55,\"totalLength\":70}")
        Map<String, Object> measurements,

        @ArraySchema(schema = @Schema(description = "상태 이상 체크", example = "NO_DEFECT"))
        List<String> conditionFlags,

        @Schema(description = "판매자 설명", example = "3회 착용했습니다.")
        String sellerDescription,

        @ArraySchema(schema = @Schema(description = "대표 OOTD"))
        List<RepresentativeOotd> representativeOotds
) {

    public static ProductDetailResponse of(
            Product product,
            ProductViewerMode viewerMode,
            List<ProductAction> availableActions,
            Map<String, Object> measurements,
            List<String> conditionFlags,
            List<ProductAttachedOotd> attachedOotds
    ) {
        return new ProductDetailResponse(
                product.getId(),
                viewerMode,
                availableActions,
                product.getProductStatus(),
                ItemSummary.from(product.getItem()),
                SellerSummary.from(product.getSeller()),
                product.getPrice(),
                product.getShippingFee(),
                product.getFeePolicy(),
                product.getMainImageUrl(),
                measurements,
                conditionFlags,
                product.getSellerDescription(),
                attachedOotds.stream()
                        .map(RepresentativeOotd::from)
                        .toList()
        );
    }

    @Schema(description = "상품 아이템 요약")
    public record ItemSummary(
            @Schema(description = "아이템 ID", example = "10")
            Long itemId,

            @Schema(description = "브랜드명", example = "Nike")
            String brandName,

            @Schema(description = "제품명", example = "Black Jacket")
            String itemName,

            @Schema(description = "사이즈 체계", example = "KR")
            String sizeSystem,

            @Schema(description = "사이즈 값", example = "100")
            String sizeValue,

            @Schema(description = "상위 카테고리", example = "아우터")
            String categoryLarge,

            @Schema(description = "상세 카테고리", example = "블루종")
            String categorySmall,

            @Schema(description = "OOTD 인증 착용 수", example = "3")
            Integer ootdVerifiedWearCount,

            @Schema(description = "위시 수", example = "12")
            Integer wishCount
    ) {

        static ItemSummary from(Item item) {
            Category category = item.getCategory();
            Category parent = category.getParent();
            String categoryLarge = parent == null ? category.getName() : parent.getName();
            String categorySmall = parent == null ? null : category.getName();
            return new ItemSummary(
                    item.getId(),
                    item.getBrandName(),
                    item.getItemName(),
                    item.getSizeSystem(),
                    item.getSizeValue(),
                    categoryLarge,
                    categorySmall,
                    item.getOotdVerifiedWearCount(),
                    item.getWishCount()
            );
        }
    }

    @Schema(description = "판매자 요약")
    public record SellerSummary(
            @Schema(description = "판매자 사용자 ID", example = "1")
            Long userId,

            @Schema(description = "판매자 아이디", example = "YuJin")
            String username,

            @Schema(description = "프로필 이미지 URL", example = "https://image.url/profile.jpg")
            String profileImageUrl,

            @Schema(description = "사용자 등급", example = "BASIC")
            UserGrade grade
    ) {

        static SellerSummary from(User seller) {
            return new SellerSummary(
                    seller.getId(),
                    seller.getUsername(),
                    seller.getProfileImageUrl(),
                    seller.getGrade()
            );
        }
    }

    @Schema(description = "대표 OOTD")
    public record RepresentativeOotd(
            @Schema(description = "OOTD ID", example = "1")
            Long ootdId,

            @Schema(description = "OOTD 이미지 URL", example = "https://image.url/ootd.jpg")
            String imageUrl,

            @Schema(description = "OOTD 생성 시각", example = "2026-07-10T12:00:00")
            LocalDateTime createdAt
    ) {

        static RepresentativeOotd from(ProductAttachedOotd attachedOotd) {
            return new RepresentativeOotd(
                    attachedOotd.getOotd().getId(),
                    attachedOotd.getOotd().getImageUrl(),
                    attachedOotd.getOotd().getCreatedAt()
            );
        }
    }
}
