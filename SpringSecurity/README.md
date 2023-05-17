# CH1 오늘날의 보안
- 스프링 시큐리티의 개념과 이를 이용해 해결할 수 있는 문제
- 소프트웨어 어플리케이션에서 보안의 의미
- 소프트웨어 보안이 중요한 이유와 관심을 가져야하는 이유
- 어플리케이션 수준의 일반적인 취약성

## 스프링 시큐리티
인증과 접근 제어를 위해 세부적인 맞춤 구성이 가능한 강력한 프레임워크로 어플리케이션에 보안을 적용하는 과정을 크게 간소화하는 프레임워크이다.

인증, 권한 부여 및 일반적인 공격에 대한 방어를 구현하는 세부적인 맞춤 구성 방법을 제공한다.

- 스프링 시큐리티를 이용해 스프링의 방식으로 어플리케이션 수준 보안을 어플리케이션에 적용할 수 있다. 스프링의 방식이란 어노테이션, 빈, SpEL(Spring Expression Language) 등을 이용하는 것이다.
- 스프링 시큐리티는 어플리케이션 수준의 보안을 구현할 수 있게 해주는 프레임워크이다. 스프링 시큐리티를 이해하고 올바르게 이용하는 것은 개발자의 책임이다. 스프링 시큐리티가 어플리케이션이나 저장 데이터나 전송 중인 민감한 데이터를 자동으로 보호해주는 것은 아니다.

## 소프트웨어 보안
현재의 소프트웨어 시스템은 특히 현재 GDPR(General Data Protection Regulations : 일반 데이터 보호 규정) 요구 사항을 고려할 때 상당 부분이 민감한 정보일 수 있는 대량의 데이터를 관리한다. 사용자가 개인적이라고 생각하는 모든 정보는 소프트웨어 어플리케이션에서 민감한 정보가 된다. 민감한 정보에는 전화번호, 이메일 주소 또는 시별 번호와 같은 무해한 정보도 있고 유출됐을 떄 위험성이 높은 신용 카드 정보 등의 더 중요하게 고려되어야 할 것들이 있다.

어플리케이션은 정보에 접근과 변경 또는 가로챌 기회가 없게 해야 하고 의도된 사용자 이외의 대상은 어떤 식으로든 데이터와 상호 작용할 수 없게 해야 한다. 이것은 광범위하게 보안으로 표현된다.

보안은 계층별로 적용되며 각 계층에 따른 접근 방식이 필요하다.
각 계층을 더 잘 보호할수록 악의적인 대상이 데이터에 접근하거나 무단 작업을 수행할 가능성이 낮아진다.

## 어플리케이션 수준 보안
어플리케이션이 실행되는 환경과 어플리케이션이 처리하고 저장하는 데이터를 보호하기 위해 해야 하는 모든 것을 나타낸다. 이것은 사용되고 영향을 받는 데이터에만 국한되는 문제가 아닌 악의적인 개인이 전체 시스템에 영향을 줄 수 있는 취약성이 있을 수 있다.

마이크로 서비스 아키텍처를 이용해 설계된 시스템에서 특히 클라우드의 여러 가용 영역에 배포하는 경우를 흔히 볼 수 있는데, 다양한 취약성이 생길 수 있으므로 주의가 필요하다. 한 계층의 보안 문제를 해결할 때는 되도록 위 계층이 존재하지 않는다고 가정하고 해결해야 한다.

가용영역은 클라우드 배포의 관점에서 별도의 데이터 센터를 말한다. 이 데이터 센터는 한 가용 영역에 장애가 발생해도 다른 가용영역에 장애가 발생할 가능성을 최소화하도록 같은 지역의 다른 데이터 센터와 지리적으로 충분히 멀리 떨어져 있다. 보안 측면에서 중요한 사실은 두 개의 서로 다른 데이터 센터 간의 트래픽이 일반적으로 공용 네트워크를 통과한다는 것이다.

## 인증과 권한 부여
사실상 거의 모든 어플리케이션에서 사용되며, 인증은 어플리케이션이 사용자(사람 또는 다른 어플리케이션)를 식별하는 방법이다. 사용자를 식별하는 목적은 나중에 그들이 무엇을 하도록 허용해야 하는지 결정하기 위한 것이다. 이것이 권한 부여이다.
권한 부여의 경우 다양한 시나리오를 위해 구현해야 하는 경우가 많다. 대부분 어플리케이션에선 사용자가 특정 기능에 대한 접근 권한을 얻는 데 제인이 있으며 접근 권한을 얻으려면 먼저 누가 접근 요청하는지 알아야 한다. 즉 인증이 필요하다.
또한 사용자가 시스템의 해당 부분을 이용하도록 허용하려면 사용자에게 어떤 이용 권리가 있는지 알아야 한다.


## 데이터 유형에 따른 보안 조치 적용
데이터 저장소에 관해서도 주의할 필요가 있다. 저장 데이터(Data at Rest)는 어플리케이션의 책임을 가중한다. 모든 데이터를 읽을 수 있는 형식으로 저장하지 말고 개인 키로 암호화한 데이터나 해시된 데이터로 저장해야 한다. 자격 증명 및 개인 키와 같은 비밀도 저장 데이터로 간주할 수 있으며, 일반적으로 이러한 데이터는 비밀 볼트에 조심히 저장해야한다.
데이터는 저장데이터(Data at Rest)와 전송 중 데이터(Data in Transit)로 구분한다. 이 맥락에서 저장 데이터는 컴퓨터 스토리지에 있는 데이터, 즉 지속된 데이터를 말하고 전송 중 데이터는 한 위치에서 다른 위치로 교환 중인 모든 데이터를 말한다. 데이터의 유형에 따라 다른 보안 조치를 적용해야 한다.

## 보안의 중요성
보안이 중요한 이유 중 가장 중요한 것은 사용자의 관점에서 보는 것으로 주로 사용자는 데이터에 접근하는 어플리케이션을 이용한다. 이러한 어플리케이션은 데이터를 변경하고 이용하며 노출한다. 같은 데이터나 작업도 사람에 따라 민감도는 다를 수 있다. 어떤 사용자는 자신의 이메일에 접근하고 메시지를 읽는 것을 더 심각하게 생각할 수 있기도 하다. 어플리케이션은 모든 것을 원하는 접근 수준까지는 보호해야 한다. 데이터와 기능을 이용해 다른 시스템에 영향을 줄 수 있는 모든 누출을 취약성으로 간주하고 해결해야 한다.
이러한 보안은 충분히 주의하지 않으면 수익성 손실을 초래하는 등 여러 사고가 발생할 수 있다.

## 웹 어플리케이션의 일반적인 보안 취약성(Vulnerability)
공격자는 공격을 시작하기 전에 어플리케이션의 취약성 먼저 파악하고 공략한다.
보통의 취약점은 인증 취약성, 세션 고정, XXS(교차 사이트 스크립팅), CSRF(사이트 간 요청 위조), 주입, 기밀 데이터 노출, 메서드 접근 제어 부족, 알려진 취약성이 있는 종속성 이용 과 같다. 이러한 항목은 어플리케이션의 수준 보안이며 대부분 스프링 시큐리티 사용으로 해결할 수 있다.

### ① 인증(Authentication)과 권한 부여(Authorization)의 취약성
사용자가 악의를 가지고 다른 사람의 기능이나 데이터에 접근할 수 있는 취약성이다. 스프링 시큐리티와 같은 프레임워크로 이러한 취약성이 발생할 우려를 줄일 수 있지만, 올바르게 이용하지 않는다면 여전히 위험성은 있다. 

- 인증 : 
어플리케이션이 이를 이용하려는 사람을 식별하는 프로세스, 어떤 사용자나 존재가 앱을 이용하려고 하면 추가 접근을 허가하기 전에 먼저 이들의 ID를 확인해야 한다. 실제 앱에서는 익명 액세스를 지원할 때도 있지만 대부분 식별된 사용자만 데이터를 이용하거나 특정 작업을 수행할 수 있다. 사용자의 ID를 확인하고 나면 권한 부여 프로세스를 시작할 수 있다.
- 권한부여 : 
인증된 호출자가 특정 기능과 데이터에 대한 이용 권리가 있는지 확인하는 프로세스다.

### ② 세션 고정(Session Fixation)
웹 어플리케이션의 더 구체적이고 심각한 약점으로 이 취약점이 존재한다면 이미 생성된 세션 ID를 재 이용해서 유효한 사용자를 가장할 수 있다. 이 취약성은 웹 어플리케이션이 인증 프로세스 중에 고유한 세선 ID를 할당하지 않아 기존 세션 ID가 재사용될 가능성이 있을 때 발생한다. 이 취약성을 악용한다면 유효한 세션 ID를 획득한 후 의도한 피해자의 브라우저기 이를 이용하게 해야 한다.

### ③ XSS(교차 사이트 스크립팅)


