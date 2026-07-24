package com.tenure.domain.ootd.dto;

import com.tenure.domain.item.entity.Category;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdSource;
import com.tenure.domain.ootd.enums.OotdTagStatus;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.enums.ProductStatus;
import com.tenure.domain.tag.entity.OotdTag;
import com.tenure.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "OOTD 상세 응답")
public record OotdDetailResponse(

        @Schema(description = "OOTD ID", example = "1")
        Long ootdId,

        @Schema(description = "OOTD 이미지 URL", example = "https://image.url/ootd.jpg")
        String imageUrl,

        @Schema(description = "게시 경로", example = "CAMERA")
        OotdSource source,

        @Schema(description = "태그 확정 상태", example = "CONFIRMED")
        OotdTagStatus tagStatus,

        @Schema(description = "태그 확정 시각", example = "2026-07-14T10:00:00")
        LocalDateTime tagConfirmedAt,

        Author author,

        @Schema(description = "좋아요 수", example = "12")
        Integer heartCount,

        @Schema(description = "저장 수", example = "4")
        Integer saveCount,

        @Schema(description = "조회 수", example = "120")
        Integer viewCount,

        @Schema(description = "현재 사용자의 좋아요 여부", example = "true")
        boolean hearted,

        @Schema(description = "현재 사용자의 저장 여부", example = "false")
        boolean saved,

        @ArraySchema(schema = @Schema(description = "확정된 아이템 태그"))
        List<TagInfo> tags
) {

    public static OotdDetailResponse of(
            Ootd ootd,
            boolean hearted,
            boolean saved,
            List<OotdTag> tags,
            Map<Long, Product> latestProductByItemId,
            long followerCount,
            long feedCount,
            boolean following
    ) {
        return new OotdDetailResponse(
                ootd.getId(),
                ootd.getImageUrl(),
                ootd.getSource(),
                ootd.getTagStatus(),
                ootd.getTagConfirmedAt(),
                Author.from(ootd.getOwner(), followerCount, feedCount, following),
                ootd.getHeartCount(),
                ootd.getSaveCount(),
                ootd.getViewCount(),
                hearted,
                saved,
                tags.stream()
                        .map(tag -> TagInfo.of(tag, latestProductByItemId.get(tag.getItem().getId())))
                        .toList()
        );
    }

    @Schema(description = "작성자 요약")
    public record Author(
            @Schema(description = "작성자 사용자 ID", example = "3")
            Long userId,

            @Schema(description = "작성자 아이디", example = "Sujun")
            String username,

            @Schema(description = "프로필 이미지 URL", example = "https://image.url/profile.jpg")
            String profileImageUrl,

            @Schema(description = "팔로워 수", example = "42")
            long followerCount,

            @Schema(description = "작성자의 활성 피드(게시물) 수", example = "17")
            long feedCount,

            @Schema(description = "현재 요청자가 작성자를 팔로우 중인지 여부", example = "true")
            boolean following
    ) {

        static Author from(User owner, long followerCount, long feedCount, boolean following) {
            return new Author(
                    owner.getId(),
                    owner.getUsername(),
                    owner.getProfileImageUrl(),
                    followerCount,
                    feedCount,
                    following
            );
        }
    }

    @Schema(description = "확정 아이템 태그")
    public record TagInfo(
            @Schema(description = "태그 ID", example = "1")
            Long tagId,

            @Schema(description = "아이템 ID", example = "10")
            Long itemId,

            @Schema(description = "바운딩 박스 X (0~1 상대좌표)", example = "0.12345")
            BigDecimal bboxX,

            @Schema(description = "바운딩 박스 Y (0~1 상대좌표)", example = "0.23456")
            BigDecimal bboxY,

            @Schema(description = "바운딩 박스 너비 (0~1 상대좌표)", example = "0.30000")
            BigDecimal bboxWidth,

            @Schema(description = "바운딩 박스 높이 (0~1 상대좌표)", example = "0.40000")
            BigDecimal bboxHeight,

            @Schema(description = "태그 라벨 텍스트", example = "블랙 자켓")
            String labelText,

            ItemInfo item,

            @Schema(description = "판매중 여부", example = "true")
            boolean onSale,

            @Schema(description = "판매 가격 (판매중일 때만 노출)", example = "50000")
            Integer price,

            @Schema(description = "구매 제안 허용 여부", example = "true")
            Boolean purchaseOfferEnabled,

            @Schema(description = "아이템 상태", example = "ON_SALE")
            ItemStatus itemStatus,

            @Schema(description = "가장 최근 판매글 상태 (판매글이 없으면 null)", example = "ON_SALE")
            ProductStatus productStatus
    ) {

        static TagInfo of(OotdTag tag, Product latestProduct) {
            Item item = tag.getItem();
            boolean onSale = latestProduct != null && latestProduct.getProductStatus() == ProductStatus.ON_SALE;
            return new TagInfo(
                    tag.getId(),
                    item.getId(),
                    tag.getBboxX(),
                    tag.getBboxY(),
                    tag.getBboxWidth(),
                    tag.getBboxHeight(),
                    tag.getLabelText(),
                    ItemInfo.from(item),
                    onSale,
                    onSale ? latestProduct.getPrice() : null,
                    item.getPurchaseOfferEnabled(),
                    item.getItemStatus(),
                    latestProduct == null ? null : latestProduct.getProductStatus()
            );
        }
    }

    @Schema(description = "태그된 아이템 정보")
    public record ItemInfo(
            @Schema(description = "아이템 ID", example = "10")
            Long itemId,

            @Schema(description = "브랜드명", example = "Nike")
            String brandName,

            @Schema(description = "제품명", example = "Black Jacket")
            String itemName,

            @Schema(description = "상위 카테고리", example = "아우터")
            String categoryLarge,

            @Schema(description = "상세 카테고리", example = "블루종")
            String categorySmall
    ) {

        static ItemInfo from(Item item) {
            Category category = item.getCategory();
            Category parent = category.getParent();
            String categoryLarge = parent == null ? category.getName() : parent.getName();
            String categorySmall = parent == null ? null : category.getName();
            return new ItemInfo(
                    item.getId(),
                    item.getBrandName(),
                    item.getItemName(),
                    categoryLarge,
                    categorySmall
            );
        }
    }
}
