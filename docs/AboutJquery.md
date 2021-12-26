### DOM

#### DOM의 개념
- 문서 객체 모델(DOM, Document Obejct Model)은 XML이나 HTML 문서에 접근하기 위한 일종의 인터페이스이다.
- 이 객체 모델은 문서 내의 모든 요소를 정의하고, 각각의 요소에 접근하는 방법을 제공한다.
- Document - 루트 요소(html) - 요소(body, head, a, p 등등) - 속성("href") - 텍스트로 이루어진다.
- W3C DOM 표준은 세 가지 모델로 구분된다.(Core DOM : 모든 문서 타입, HTML DOM, XML DOM)
- 여기서 배울 모든 HTML 요소는 HTML DOM을 통해 접근할 수 있다. 

<br>

#### Document 객체
- Document 객체는 웹 페이지 그 자체를 의미한다. 
- 웹 페이지에 존재하는 HTML 요소에 접근하고자 할 때는 반드시 Document 객체부터 시작해야 한다.

<br>

#### Document 메소드 
- Document 객체는 HTML 요소와 관련된 작업을 도와주는 다양한 메서드를 제공한다.
- HTML 요소의 선택, HTML 요소의 생성, 이벤트 핸들러 추가, HTML 객체의 선택

<br>

#### HTML 요소의 선택 
- document.getElementsByTagsName(태그이름) : 해당 태그 이름의 요소를 모두 선택함
- document.getElementById(아이디) : 해당 아이디의 요소를 선택함. 구체적인 id가 없다면 Null 반환
- document.getElementsByClassName(클래스이름) : 해당 클래스에 속한 요소를 모두 선택함. HTMLCollection을 리턴(name, id로 접근 가능)
- document.getElementsByName(name속성값) : 해당 name 속성값을 가지는 요소를 모두 선택함
- document.querySelectorAll(선택자) : 해당 (CSS)선택자로 선택되는 요소를 모두 선택함. NodeList를 리턴함(인덱스 번호로만 접근 가능)
    - 많은 사람들이 querySelector, getElementById의 차이를 모른다.
    - 현재는 querySelector를 사용하지 않는다고 한다.
    - querySelector를 사용하면 우리가 원하는 엘리먼트를 더 구체적이고 한정적으로 선택할 수 있다.
    - querySelector에서는 name, #name을 통해 id, class에 접근할 수 있다.
    
<br>

#### HTML 요소의 생성
- document.createElement(HTML 요소) : 지정된 HTML 요소를 생성함.
- document.write(텍스트) : HTML 출력 스트림을 통해 텍스츠를 출력함.

<br>

#### HTML 이벤트 핸들러 추가
- document.getElementById(아이디).onclick = function() {실행 코드} : 클릭 이벤트와 연결될 이벤트 핸들러 코드를 추가

<br>

#### HTML 객체의 선택
- HTML DOM에서 제공하는 객체 집합(object collection)을 이용하면 HTML 객체를 손쉽게 선택할 수 있다.
- ex) document.forms : <form>요소를 모두 반환 

<br>

### DOM 요소
#### DOM 요소의 선택
- HTML 요소를 다루기 위해서는 우선 해당 요소를 선택해야만 한다. 방법은 아래와 같다.
1. HTML 태그 이름을 이용한 선택(getElementsByTagNames)
2. 아이디를 이용한 선택(getElementById) : 해당 아이디를 가진 요소 중 첫 번째 요소 하나만을 선택한다.
3. 클래스를 이용한 선택(getElementByClassName()) : 해당 클래스를 가진 모든 요소를 선택한다.
4. name 속성을 이용한 선택 : 해당 name을 가진 모든 요소를 선택
5. css 선택자(selector)를 이용한 선택 : 아이디, 클래스, 속성, 속성값 등을 이용하여 한 개(querySelector), 여러 개(querySelectorAll) 선택한다.
6. HTML 객체 집합(object collection)을 이용한 선택 : document.title 등의 객체 집합으로 요소를 선택한다.

<br>

#### DOM 요소의 내용 변경
- HTML DOM을 이용하면 HTML 요소의 내용(content)이나 속성값 등을 손쉽게 변경할 수 있따.
- 가장 쉬운 방법은 innerHTML 프로퍼티를 이용하는 것이다.
- HTML 요소의 속성 이름(href 등)으로 직접 속성값을 변경할 수 있다.

<br>

#### DOM 요소의 스타일 변경
- style 프로퍼티를 통해 HTML 요소에 CSS 스타일을 적용한다.
- 







- 제이쿼리(오픈소스 자바스크립트 라이브러리)
    - 웹 사이트에 자바스크립트를 손쉽게 활용할 수 있게 해준다.
    - 짧고 단순한 코드로 웹 페이지에 다양한 효과나 연출을 적용할 수 있다.
    - HTML DOM을 쉽게 조작할 수 있으며, CSS 스타일도 간단히 적용할 수 있다.
    - 자바스크립트 라이브러리이므로, .js 파일 형태로 존재한다. 따라서 웹 페이지에서 사용하기 위해 제이쿼리 파일을 웹 페이지에 먼저 로드해야한다.
    - 파일을 다운받아 로드할 수 있다. 또는 CDN(Content Delivery Network)를 통해 로드한다.

<br>

- 제이쿼리 기본 문법
    - $(선택자).동작함수();
    - 달러 기호는 제이쿼리를 의미하고, 제이쿼리에 접근할 수 있게 해주는 식별자이다.
    - 선택자를 이용하여 원하는 HTML요소를 선택하고, 동작 함수를 정의하여 선택된 요소에 원하는 동작을 설정한다.

- $() 함수
    - 선택된 HTML 요소를 제이쿼리에서 이용할 수 있는 형태로 생성해주는 역할을 한다.
    - 이 함수의 인수로는 HTML 태그 이름 뿐 아니라, CSS 선택자를 전달하여 특정 HTML 요소를 선택할 수 있다.
    - 이러한 함수를 통해 생성된 요소를 제이쿼리 객체(jQuery Object)라고 한다.
    - 이렇게 생성된 객체의 메서드를 통해 여러 동작을 설정할 수 있다.
    
- Document 객체의 ready() 메서드
    - 자바스크립트 코드는 웹 브라우저가 문서의 모든 요소를 로드한 뒤에 실행되어야 한다.
    - 