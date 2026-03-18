package org.codeiteam3.findex.indexdata.repository.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.indexdata.entity.IndexData;
import org.codeiteam3.findex.indexdata.repository.IndexDataRepositoryQueryDsl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.codeiteam3.findex.indexdata.entity.QIndexData.indexData;
import static org.codeiteam3.findex.indexinfo.entity.QIndexInfo.indexInfo;

@Repository
@RequiredArgsConstructor
public class IndexDataRepositoryQueryDslImpl implements IndexDataRepositoryQueryDsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Long countElements(UUID indexInfoId, LocalDate startDate, LocalDate endDate) {
        Long count = queryFactory
                .select(indexData.count())
                .from(indexData)
                .where(
                        indexInfoIdEq(indexInfoId),
                        baseDateGoe(startDate),
                        baseDateLoe(endDate)
                )

                .fetchOne(); // 쿼리 실행 후 결과를 한 건 반환

        return count != null ? count : 0L;
    }

    @Override
    public Slice<IndexData> findAllByBaseDate(
            UUID indexInfoId,
            LocalDate startDate,
            LocalDate endDate,
            UUID idAfter,
            LocalDate localDateCursor,
            Sort.Direction normalizedDirection,
            Pageable pageable
    ) {
        List<IndexData> content = queryFactory
                .select(indexData).from(indexData)
                .where(
                        indexInfoIdEq(indexInfoId),
                        baseDateGoe(startDate),
                        baseDateLoe(endDate),
                        baseDateCursorCondition(idAfter, localDateCursor, normalizedDirection)
                )
                .orderBy(
                        normalizedDirection.isDescending() ? indexData.baseDate.desc() : indexData.baseDate.asc(),
                        normalizedDirection.isDescending() ? indexData.id.desc() : indexData.id.asc()
                )
                .limit(pageable.getPageSize() + 1L)
                .fetch(); // 쿼리 실행 후 결과를 리스트로 반환

        return toSlice(content, pageable);
    }

    @Override
    public Slice<IndexData> findAllByBigDecimal(
            UUID indexInfoId,
            LocalDate startDate,
            LocalDate endDate,
            UUID idAfter,
            BigDecimal bigDecimalCursor,
            Sort.Direction normalizedDirection,
            String normalizedSortField,
            Pageable pageable
    ) {
        // 동적 쿼리는 타입 명시 필수
        NumberPath<BigDecimal> sortFieldPath = getBigDecimalPath(normalizedSortField);

        List<IndexData> content = queryFactory
                .select(indexData).from(indexData)
                .where(
                        indexInfoIdEq(indexInfoId),
                        baseDateGoe(startDate),
                        baseDateLoe(endDate),
                        bigDecimalCursorCondition(sortFieldPath, idAfter, bigDecimalCursor, normalizedDirection)
                )
                .orderBy(
                        normalizedDirection.isDescending() ? sortFieldPath.desc() : sortFieldPath.asc(),
                        normalizedDirection.isDescending() ? indexData.id.desc() : indexData.id.asc()
                )
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        return toSlice(content, pageable);
    }

    @Override
    public Slice<IndexData> findAllByLong(
            UUID indexInfoId,
            LocalDate startDate,
            LocalDate endDate,
            UUID idAfter,
            Long longCursor,
            Sort.Direction normalizedDirection,
            String normalizedSortField,
            Pageable pageable
    ) {
        // 동적 쿼리는 타입 명시 필수
        NumberPath<Long> sortFieldPath = getLongPath(normalizedSortField);

        List<IndexData> content = queryFactory
                .select(indexData).from(indexData)
                .where(
                        indexInfoIdEq(indexInfoId),
                        baseDateGoe(startDate),
                        baseDateLoe(endDate),
                        longCursorCondition(sortFieldPath, idAfter, longCursor, normalizedDirection)
                )
                .orderBy(
                        normalizedDirection.isDescending() ? sortFieldPath.desc() : sortFieldPath.asc(),
                        normalizedDirection.isDescending() ? indexData.id.desc() : indexData.id.asc()
                )
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        return toSlice(content, pageable);
    }

    private Slice<IndexData> toSlice(List<IndexData> content, Pageable pageable) {
        boolean hasNext = content.size() > pageable.getPageSize();

        if (hasNext) {
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    // 특정 기간 내의 지수 데이터 조회
    public List<IndexData> findChartDataByPeriod(UUID indexInfoId, LocalDate startDate, LocalDate endDate) {

        return queryFactory
                .selectFrom(indexData)
                .where(
                        indexInfoIdEq(indexInfoId),
                        baseDateGoe(startDate),
                        baseDateLoe(endDate)
                )
                .orderBy(indexData.baseDate.desc())
                .fetch();
    }

    @Override
    // 전체 지수 조회 시
    // 각 지수별로 타겟 날짜 이전의 가장 최신 날짜를 찾은 뒤, 원본 테이블과 조인해서 데이터를 가져옴
    public List<IndexData> findLatestDataOfAllIndexesOnOrBefore(LocalDate targetDate) {
        // 서브 쿼리 전용
        // 변수명 분리 중요
        org.codeiteam3.findex.indexdata.entity.QIndexData subIndexData =
                new org.codeiteam3.findex.indexdata.entity.QIndexData("sub");
        return queryFactory
                .selectFrom(indexData)
                .join(indexData.indexInfo, indexInfo).fetchJoin()
                .where(indexData.baseDate.eq(
                        JPAExpressions
                                .select(subIndexData.baseDate.max())
                                .from(subIndexData)
                                .where(
                                        subIndexData.indexInfo.eq(indexInfo),
                                        subIndexData.baseDate.loe(targetDate)
                                )
                ))
                .fetch();
    }

    @Override
    // 즐겨찾기 되어있는 지수 조회
    public List<IndexData> findFavoriteDataOnOrBefore(LocalDate targetDate) {
        // 서브쿼리 전용
        org.codeiteam3.findex.indexdata.entity.QIndexData subIndexData =
                new org.codeiteam3.findex.indexdata.entity.QIndexData("sub");
        return queryFactory
                .selectFrom(indexData)
                .join(indexData.indexInfo,indexInfo).fetchJoin()
                .where(
                        indexInfo.favorite.eq(true),
                        indexData.baseDate.eq(
                                JPAExpressions
                                        .select(subIndexData.baseDate.max())
                                        .from(subIndexData)
                                        .where(
                                                subIndexData.indexInfo.eq(indexInfo),
                                                subIndexData.baseDate.loe(targetDate)
                                        )
                        )
                )
                .fetch();
    }

    @Override
    // 특정 지수의 가장 최신 날짜 조회
    public Optional<LocalDate> findLatestBaseDateByIndexInfoId(UUID indexInfoId) {
        LocalDate localDate = queryFactory
                .select(indexData.baseDate.max())
                .from(indexData)
                .where(indexInfoIdEq(indexInfoId))
                .fetchOne();

        return Optional.ofNullable(localDate);
    }
    // `eq` -> `=`
    private BooleanExpression indexInfoIdEq(UUID indexInfoId) {
        return indexInfoId !=null ? indexData.indexInfo.id.eq(indexInfoId) : null;
    }

    // `goe` -> `>=`
    private BooleanExpression baseDateGoe(LocalDate startDate) {
        return startDate != null ? indexData.baseDate.goe(startDate) : null;
    }
    // `loe` -> `<=`
    private BooleanExpression baseDateLoe(LocalDate endDate) {
        return endDate != null ? indexData.baseDate.loe(endDate) : null;
    }

    private BooleanExpression baseDateCursorCondition(
            UUID idAfter,
            LocalDate localDateCursor,
            Sort.Direction direction
    ) {
        if (localDateCursor == null) {
            return null;
        }

        // `lt` -> `<` 미만
        if (direction.isDescending()) {
            return indexData.baseDate.lt(localDateCursor)
                    .or(indexData.baseDate.eq(localDateCursor).and(indexData.id.lt(idAfter)));
        }

        // `gt` -> `>` 초과
        return indexData.baseDate.gt(localDateCursor)
                .or(indexData.baseDate.eq(localDateCursor).and(indexData.id.gt(idAfter)));
    }

    private BooleanExpression bigDecimalCursorCondition(
            NumberPath<BigDecimal> sortFieldPath,
            UUID idAfter,
            BigDecimal bigDecimalCursor,
            Sort.Direction normalizedDirection
    ) {
        if (bigDecimalCursor == null) {
            return null;
        }

        // `lt` -> `<` 미만
        if (normalizedDirection.isDescending()) {
            return sortFieldPath.lt(bigDecimalCursor)
                    .or(sortFieldPath.eq(bigDecimalCursor).and(indexData.id.lt(idAfter)));
        }

        // `gt` -> `>` 초과
        return sortFieldPath.gt(bigDecimalCursor)
                .or(sortFieldPath.eq(bigDecimalCursor).and(indexData.id.gt(idAfter)));
    }

    private BooleanExpression longCursorCondition(
            NumberPath<Long> sortFieldPath,
            UUID idAfter,
            Long longCursor,
            Sort.Direction normalizedDirection
    ) {
        if (longCursor == null) {
            return null;
        }

        // `lt` -> `<` 미만
        if (normalizedDirection.isDescending()) {
            return sortFieldPath.lt(longCursor)
                    .or(sortFieldPath.eq(longCursor).and(indexData.id.lt(idAfter)));
        }

        // `gt` -> `>` 초과
        return sortFieldPath.gt(longCursor)
                .or(sortFieldPath.eq(longCursor).and(indexData.id.gt(idAfter)));
    }

    private NumberPath<BigDecimal> getBigDecimalPath(String normalizedSortField) {
        return switch (normalizedSortField) {
            case "marketPrice" -> indexData.marketPrice;
            case "closingPrice" -> indexData.closingPrice;
            case "highPrice" -> indexData.highPrice;
            case "lowPrice" -> indexData.lowPrice;
            case "versus" -> indexData.versus;
            case "fluctuationRate" -> indexData.fluctuationRate;
            default -> throw new IllegalArgumentException("BigDecimal 타입의 정렬 필드가 아닙니다.");
        };
    }

    private NumberPath<Long> getLongPath(String normalizedSortField) {
        return switch (normalizedSortField) {
            case "tradingQuantity" -> indexData.tradingQuantity;
            case "tradingPrice" -> indexData.tradingPrice;
            case "marketTotalAmount" -> indexData.marketTotalAmount;
            default -> throw new IllegalArgumentException("Long 타입의 정렬 필드가 아닙니다.");
        };
    }

}
