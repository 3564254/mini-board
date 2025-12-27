// [리팩토링 제안]
// 1. id와 createdAt은 객체가 생성된 후 변경되어서는 안 되므로 final로 선언하고 setter를 제거하여 불변성을 보장하는 것이 좋습니다.
// 2. createdAt과 updatedAt 필드는 생성자나 특정 메서드 호출 시 자동으로 초기화되도록 설정하는 것을 고려해볼 수 있습니다. (예: @PrePersist, @PreUpdate 어노테이션 활용)
// 3. @Data 어노테이션은 모든 필드에 대해 getter/setter를 생성하고, equals, hashCode, toString까지 오버라이드하여 예상치 못한 동작을 유발할 수 있습니다. 필요한 어노테이션(@Getter, @Setter 등)만 선별적으로 사용하는 것이 더 안전합니다.

package com.myproject.mini_board.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public abstract class BaseEntity {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public BaseEntity(Long id) {
        this.id = id;
    }
}
