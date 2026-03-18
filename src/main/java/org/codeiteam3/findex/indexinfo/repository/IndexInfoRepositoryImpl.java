package org.codeiteam3.findex.indexinfo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;
import org.codeiteam3.findex.indexinfo.entity.QIndexInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

import static org.codeiteam3.findex.indexinfo.entity.QIndexInfo.indexInfo;

@RequiredArgsConstructor//final,@NonNull에 대해 자동으로 생성자 붙여줌.
public class IndexInfoRepositoryImpl implements IndexInfoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Long countElements(
            String indexClassification,
            String indexName,
            Boolean favorite
    ) {

        QIndexInfo indexInfo = QIndexInfo.indexInfo;

        //1.필터
        BooleanBuilder builder = buildFilter(indexInfo,indexClassification,indexName,favorite);

        Long count = queryFactory
                .select(indexInfo.count())
                .from(indexInfo)
                .where(builder)
                .fetchOne();

        return count != null ? count : 0L;//count null값 가능

    }


    @Override
    public Slice<IndexInfo> findAllByString(
            String indexClassification,
            String indexName,
            Boolean favorite,
            UUID idAfter,
            String cursor,
            String sortField,
            Sort.Direction normalizedDirection,
            Pageable pageable
    ){
        QIndexInfo indexInfo = QIndexInfo.indexInfo;//Entity를 QueryDSL용 객체로 변환한것.

        //1.필터
        BooleanBuilder builder = buildFilter(indexInfo,indexClassification,indexName,favorite); //WHERE 조건을 쌓기위한 도구

        //2.정렬
        StringPath sortPath;
        if("indexName".equals(sortField)){
            sortPath = indexInfo.indexName;
        }
        else{
            sortPath = indexInfo.indexClassification;
        }

        //3.커서
        BooleanExpression cursorCondition = stringCursorCondition(
                sortPath,
                idAfter,
                cursor,
                normalizedDirection
        );

        if (cursorCondition != null) {
            builder.and(cursorCondition);
        }

        //4.조회 + 정렬
        List<IndexInfo> content = queryFactory
                .selectFrom(indexInfo)
                .where(builder)
                .orderBy(
                        normalizedDirection.isDescending() ? sortPath.desc():sortPath.asc(),
                        normalizedDirection.isDescending() ? indexInfo.id.desc(): indexInfo.id.asc()
                )
                .limit(pageable.getPageSize() + 1)
                .fetch();

        //5. Slice 처리
        boolean hasNext = content.size() > pageable.getPageSize();//다음 페이지가 있냐를 확인

        if(hasNext){
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
        //Slice<> 내부는 content,pageable,hasNext로 이루어져있다.

    }

    @Override
    public Slice<IndexInfo> findAllByInteger(
            String indexClassification,
            String indexName,
            Boolean favorite,
            UUID idAfter,
            Integer cursor,
            Sort.Direction normalizedDirection,
            Pageable pageable) {

        QIndexInfo indexInfo = QIndexInfo.indexInfo;//Entity를 QueryDSL용 객체로 변환한것.

        //1.필터
        BooleanBuilder builder = buildFilter(indexInfo,indexClassification,indexName,favorite);

        //2.정렬
        NumberPath<Integer> sortPath = indexInfo.employedItemsCount;

        //3.커서
        BooleanExpression cursorCondition = integerCursorCondition(
                sortPath,
                idAfter,
                cursor,
                normalizedDirection
        );

        if (cursorCondition != null) {
            builder.and(cursorCondition);
        }

        //4.조회 + 정렬
        List<IndexInfo> content = queryFactory
                .selectFrom(indexInfo)
                .where(builder)
                .orderBy(
                        normalizedDirection.isDescending() ? sortPath.desc():sortPath.asc(),
                        normalizedDirection.isDescending() ? indexInfo.id.desc(): indexInfo.id.asc()
                )
                .limit(pageable.getPageSize() + 1)
                .fetch();

        //5. Slice 처리
        boolean hasNext = content.size() > pageable.getPageSize();//다음 페이지가 있냐를 확인

        if(hasNext){
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanBuilder buildFilter(
            QIndexInfo indexInfo,
            String indexClassification,
            String indexName,
            Boolean favorite
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        if (indexClassification != null && !indexClassification.isBlank()) {
            builder.and(indexInfo.indexClassification.contains(indexClassification));
        }
        if (indexName != null && !indexName.isBlank()) {
            builder.and(indexInfo.indexName.contains(indexName));
        }
        if (favorite != null) {
            builder.and(indexInfo.favorite.eq(favorite));
        }

        return builder;
    }

    private BooleanExpression stringCursorCondition(
            StringPath sortFieldPath,
            UUID idAfter,
            String stringCursor,
            Sort.Direction normalizedDirection
    ) {
        if (stringCursor == null) {
            return null;
        }

        if (normalizedDirection.isDescending()) {
            BooleanExpression condition = sortFieldPath.lt(stringCursor);

            if (idAfter != null) {
                condition = condition.or(
                        sortFieldPath.eq(stringCursor)
                                .and(indexInfo.id.lt(idAfter))
                );
            }

            return condition;
        }

        BooleanExpression condition = sortFieldPath.gt(stringCursor);

        if (idAfter != null) {
            condition = condition.or(
                    sortFieldPath.eq(stringCursor)
                            .and(indexInfo.id.gt(idAfter))
            );
        }

        return condition;
    }


    private BooleanExpression integerCursorCondition(
            NumberPath<Integer> sortFieldPath,
            UUID idAfter,
            Integer integerCursor,
            Sort.Direction normalizedDirection
    ) {
        if (integerCursor == null) {
            return null;
        }

        if (normalizedDirection.isDescending()) {
            BooleanExpression condition = sortFieldPath.lt(integerCursor);

            if (idAfter != null) {
                condition = condition.or(
                        sortFieldPath.eq(integerCursor)
                                .and(indexInfo.id.lt(idAfter))
                );
            }

            return condition;
        }

        BooleanExpression condition = sortFieldPath.gt(integerCursor);

        if (idAfter != null) {
            condition = condition.or(
                    sortFieldPath.eq(integerCursor)
                            .and(indexInfo.id.gt(idAfter))
            );
        }

        return condition;
    }






}
