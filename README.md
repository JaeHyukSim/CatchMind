# catch-mind
### [project-study][project-study-link]

[project-study-link]: https://github.com/JaeHyukSim/project-study "Go project study!"

----------

### 최대 여섯명의 사람들과 그림 그리기를 통해 각자의 생각을 알아맞추는 JAVA 게임.

----------

### 기술 스택과 사용 도구
- Java
- Eclipse
- DB(DB는 따로 사용하지 않고, 파일 입출력을 이용)

----------

### catch-mind를 통해 배우기
1. TCP socket programming을 직접 구현함으로써 네트워크에 필요한 동작 원리, 소스코드의 이해도를 향상시킨다.
2. Design pattern을 공부하면서 여러가지 패턴들을 적용하며 프로그램을 구조화 시킨다.
    + Singleton Pattern으로 하나만 존재해야 하는 객체들인 화면, 사용자 등을 구현하였다. 
    + Observer Pattern으로 사용자가 서버에 접속 후, 대기실, 게임방에서 한 그룹으로 채팅을 하거나, 게임을 진행할 수 있도록 구현하였다. 
    + Strategy pattern으로 User forms를 Interface를 활용해서 쉽게 전환하도록 하였다.
3. 다양한 알고리즘을 적용하여 프로그램을 제작하였다.
    + 그래픽 알고리즘 - 보간법을 사용하여 (x,y) 2차원 좌표평면에서의 점을 끊김없이 연결하도록 보간법을 이용해 x,y좌표를 부드럽게 연결하였다. 
    + 게임 진행 로직에서, 문제 출제자가 그림을 그리는 과정에서 메모리를 아끼기 위해 Sliding window기법으로 최소한의 메모리만 사용하였다.
4. 게임 로직을 구현한다. 게임을 진행하면서 정답을 맞출 때, 타이머가 흐를 때, 채팅할 때 등 다양한 상황을 쓰레드를 활용해 유기적으로 연결하여 논리적인 사고력을 길렀다.
특히, 앞으로 thread pool을 공부하여 좀 더 효과적으로 thread를 관리하는 방법을 공부할 예정이다(JSOUP을 이용한 Web crawling 구현에서 사용해보자)

-----------

### 나의 담당 부분과 기능 구현
1. 로그인 구현 : 모든 유효성 검사 수행, 2 JSON형태로 저장되는 User 정보-다음 공부해야 할 부분 : User 정보 암호화하기
2. 그래픽 알고리즘 - 보간법, sliding algorithm
3. 채팅 구현 - Observer Pattern
4. 게임 진행 로직
5. 점수와 레벨 반영

-----------

### 동작시키는 방법
1. eclipse에 pull
2. serverCatchmindFinal의 Server.java 실행
3. clientCatchmindFinal의 Client.java 실행

-----------

### 시연 영상
[catch-mind-login 보러가기!][catchmind-login-link]

[catchmind-login-link]: https://www.youtube.com/watch?v=t8yO0Vgn35Y "Go login form"

[catch-mind-waiting room and game room 보러가기!][catchmind-wait-link]

[catchmind-wait-link]: https://www.youtube.com/watch?v=6aucj54OD7I "Go waiting, gaming room form!"

[catch-mind-game 보러가기!!!][catchmind-game-link]

[catchmind-game-link]: https://www.youtube.com/watch?v=i-aK17CDxKg "Go catch mind!"
-----------
