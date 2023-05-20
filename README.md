# Garbage_Collection

### 목차

- Garbage Collection(GC)란?
- GC의 필요성
- GC의 매커니즘
- GC를 제대로 동작하기 위한 코드
- GC 사용시에도 메모리 leak 발생하는 경우

---

## 1. Garbage Collection(GC)란?
- 컴퓨터 프로그래밍에서 메모리 관리 기술 중 하나
- 프로그램이 동적으로 할당한 메모리 중에서 더 이상 사용되지 않는 메모리를 자동으로 식별, 해제하는 프로세스

Java에서는 개발자가 메모리를 직접 해제할 필요가 없이 JVM의 GC가 불필요한 메모리를 정리해 줍니다.


JVM의 Heap영역은 처음 설계될 때 다음의 2가지를 전제(Weak Generational Hypothesis)로 설계 되었습니다.
- 대부분의 객체는 금방 접근 불가능한 상태(Unreachable)가 됩니다.
- 오래된 객체에서 새로운 객체로의 참조는 아주 적게 존재합니다.

=> 즉, 객체는 대부분 일회성이며, 메모리에 오랫동안 남아있는 경우는 드물다는 것입니다. 그래서 객체의 생존 기간에 따라 물리적인 Heap영역을 나누고 Young과 Old 총 2가지 영역으로 설계되었습니다.

![Java GC](https://github.com/homi5435/Garbage_Collection/assets/106005292/01f5233f-4403-41a1-9baa-cfc68457156a)


출처 - https://code-factory.tistory.com/48


***Young 영역(Young Generation)***
- 새롭게 생성된 객체가 할당(Allocation)되는 영역
- 대부분의 객체가 금방 Unreachable 상태가 되기 때문에, 많은 객체가 Young 영역에 생성되었다가 사라집니다.
- Young 영역에 대한 가비지 컬렉션(Garbage Collection)을 Minor GC라고 부릅니다.
- ***eden 영역***: 새로 생성된 객체가 할당(Allocation)되는 영역
- ***Survivor 영역***: 최소 1번의 GC 이상 살아남은 객체가 존재하는 영역


***Old 영역(Old Generation)***
- Young영역에서 Reachable 상태를 유지하여 살아남은 객체가 복사되는 영역
- Young 영역보다 크게 할당되며, 영역의 크기가 큰 만큼 가비지는 적게 발생합니다.
- Old 영역에 대한 가비지 컬렉션(Garbage Collection)을 Major GC라고 부릅니다.


---
## 2. GC의 필요성

### 개발자가 직접 메모리를 할당, 해제한다면 생기는 문제점
- **memory leak 발생**: 필요없는(사용하지 않는) 메모리를 해제해주지 않으면 memory leak이 발생합니다.

- 사용 중이던 메모리를 해제해버리면 프로그램이 중단되고, 데이터가 손실될 수 있습니다.

### GC(Garbage Collection)을 사용함으로써 생기는 이점
1. **메모리 누수 방지**

      : GC는 프로그램이 더 이상 사용하지 않는 메모리를 자동으로 식별하고 해제합니다. 이로 인해 메모리 누수를 방지할 수 있습니다.
      
2. **안정성과 신뢰성 향상**
 
      : GC는 자동으로 메모리를 관리하기 때문에 메모리 해제 오류를 방지할 수 있습니다. 이로 인해 프로그램의 안정성과 신뢰성이 향상됩니다. 메모리 누수로 인한 애플리케이션 충돌이나 예기치 않은 동작을 방지할 수 있습니다.
      
3. **생산성 향상**


      : GC를 사용하면 개발자는 명시적으로 메모리 관리에 신경을 쓰지 않아도 됩니다. 이로 인해 개발자는 메모리 할당 및 해제와 관련된 복잡한 로직을 작성하는 시간과 노력을 절약할 수 있습니다. 또한, GC는 동적 메모리 할당을 간편하게 만들어 프로그래밍 생산성을 향상시킵니다.

**=> Garbage Collection은 사용자에게 경제적, 효율적 측면에서 다양한 이점을 제공하기 때문에 필수적인 기능이라고 볼 수 있습니다.**

---
## 3. GC의 매커니즘

### Garbage Collection의 동작 원리

**1. Reachability Analysis (도달 가능성 분석)**
- GC는 메모리 내의 객체들 중에서 더 이상 참조되지 않는 객체인 가비지(garbage)를 식별
- 이를 위해 GC는 도달 가능성 분석을 수행

**2. Marking(표시)**
- 메모리 내의 모든 객체를 검사하며, 도달 가능한 객체들을 표시
- 객체의 헤더에 표시 비트(bit)를 사용하여 객체를 표시

**3. Sweep(스위핑)**
- 표시되지 않은 (도달 불가능한) 객체들을 식별하여 해제
- 이 단계에서는 가비지 객체들의 메모리를 회수

**4. Compaction(압축, 옵션)**
- 선택적으로, GC는 메모리 조각화를 해결하기 위해 압축 단계를 수행



### Minor GC 동작원리
![MinorGC](https://github.com/homi5435/Garbage_Collection/assets/106005292/6468dd48-58ac-4f75-be0b-b5e64e5f6b27)

1. 인스턴스가 계속 생성되어 ***Eden*** 영역이 포화
2. ***Stop the world*** -> ***Mark and Sweep*** 실행
3. 2.에서 살아남은 객체가 첫 ***Survivor*** 영역으로 이동
4. 첫 ***Survivor*** 영역 포화 -> ***Mark and Sweep***으로 살아남은 객체가 두번째 ***Survivor*** 영역으로 이동
5. 일정 횟수(age) 이상 살아남은 객체를 ***Old Generation*** 영역으로 이동

**※Stop The World※**
- GC 실행을 위해 JVM이 애플리케이션의 실행을 멈추는 작업
- GC를 실행하는 쓰레드를 제외한 모든 쓰레드들의 작업 중단

---
## 4. GC를 제대로 동작하기 위한 코드

**Java에서 GC가 제대로 동작하기 위한 일반적인 가이드라인**
1. 불필요한 객체 참조 해제
2. 대용량 데이터 구조 사용 시 주의
3. 메모리 누수 확인
4. 메모리 사용량 최적화
5. finalize()메소드 사용 주의
6. 특정 GC 알고리즘 및 설정 사용

```Java
public class GC_java {
    public static void main(String[] args) {
        // 객체 생성
        GC obj1 = new GC();
        GC obj2 = new GC();

        // obj1이 더 이상 참조되지 않도록 null로 설정
        obj1 = null;

        // GC 호출
        System.gc();

        // 잠시 대기
        try {
            Thread.sleep(1000); // 1초 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 메모리 상태 출력
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used Memory: " + usedMemory + " bytes");
    }
}

class GC {
    // finalize() 메소드 재정의
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Finalize method called");
        super.finalize();
    }
}
```
**이 코드를 실행하면 GC가 호출되고, 'finalize()'메소드가 호출 되는 것을 확인할 수 있으며, 메모리 상태 출력을 통해 GC에 의해 메모리가 회수되는 것을 확인할수 있습니다.**


---
## 5. GC 사용시에도 메모리 leak 발생하는 경우

### 메모리 누수(memory leak)
- 더 이상 사용되지 않는 객체들이 **가비지 컬렉터(GC)** 에 의해 회수되지 않고 계속 누적이 되는 현상을 말합니다. 

```java
import java.util.ArrayList;
import java.util.List;

public class GC_Leak {
    private static List<Integer> list = new ArrayList<>();

    public static void main(String[] args) {
        while (true) {
            // 매 반복마다 계속해서 새로운 객체를 리스트에 추가
            List<Integer> newList = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                newList.add(i);
            }
            list.addAll(newList);
        }
    }
}
```
**새로운 ArrayList 객체를 생성하고 이를 list라는 정적 리스트에 추가하는 작업을 반복합니다. 이러한 반복 작업은 무한 루프로 실행되기 때문에 GC는 메모리 누수를 해결할 수 없으며, GC는 list에 저장된 객체들을 계속 유지해야 하므로 메모리가 계속 증가하게 됩니다.**

이러한 예시를 제외하고도 **GC**가 되지 않는 **루트 참조 객체**는 크게 3가지가 있습니다.
1. **Static 변수**에 의한 객체 참조
2. 모든 현재 자바 스레드 스택내의 **지역 변수, 매개 변수**에 의한 객체 참조
3. **JNI 프로그램**에 의해 동적으로 만들어지고 제거되는 **JNI global 객체** 참조

이 외에 여러가지 방법으로 메모리 누수가 발생하는 **패턴**들이 있습니다.

마지막으로, 메모리 누수를 방지하기 위해서는 **불필요한 객체 참조를 해제**하고, **사용하지 않는 자원을 명시적으로 해제**하는 등의 조치를 취해야 합니다.
