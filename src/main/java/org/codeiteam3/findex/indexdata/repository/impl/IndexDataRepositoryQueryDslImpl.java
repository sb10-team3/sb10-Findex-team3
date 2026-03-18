package org.codeiteam3.findex.indexdata.repository.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.indexdata.entity.IndexData;
import org.codeiteam3.findex.indexdata.repository.IndexDataRepositoryQueryDsl;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.codeiteam3.findex.indexdata.entity.QIndexData.indexData;
import static org.codeiteam3.findex.indexinfo.entity.QIndexInfo.indexInfo;

@RequiredArgsConstructor
@Repository
public class IndexDataRepositoryQueryDslImpl implements IndexDataRepositoryQueryDsl {

    private final JPAQueryFactory queryFactory;
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
}
