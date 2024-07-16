package com.safety.law;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class LawApplicationTests {

    @Test
    public void imgRegexRemove(){

        String aa = """
            비계발판의 재료는 다음 각 호에 규정된 규격에 적합한 것이어야 한다.  1. 비계발판은 목재 또는 합판을 사용하여야 하며, 기타자재를 사용할 경우에는 별도의 안전조치를 하여야 한다.  2. 제재목인 경우에 있어서는 장섬유질의 경사가 (그림 1)과 같이 1：15 이하이어야 하고 충분히 건조된 것(함수율 15～20 퍼센트 이내)을 사용하여야 하며 변형, 갈라짐, 부식 등이 있는 자재를 사용해서는 아니된다.  3. 재료의 강도상 결점은 다음 각 목에 따른 검사에 적합하여야 한다.    가. 발판의 폭과 동일한 길이내에 있는 결점치수의 총합이 발판폭의 1/4을 초과하지 않을 것    나. 결점 개개의 크기가 발판의 중앙부에 있는 경우 발판폭의 1/5, 발판의 갓부분에 있을 때는 발판폭의 1/7을 초과하지 않을 것    다. 발판의 갓면에 있을 때는 발판두께의 1/2을 초과하지 않을 것    라. 발판의 갈라짐은 발판폭의 1/2을 초과해서는 아니되며 철선, 띠철로 감아서 보존할 것(그림 2)  4. 비계발판의 치수는 폭이 두께의 5～6배 이상이어야 하며 발판폭은 40센티미터 이상, 두께는 3.5센티미터 이상, 길이는 3.6미터 이내이어야 한다.  5. 비계발판은 하중과 간격에 따라서 응력의 상태가 달라지므로〈표 1〉에 의한 허용 응력을 초과하지 않도록 설계하여야 한다.
            <img id="55828929"></img>〈표 1〉비계발판 작업으로서 목재의 허용응력단위：(킬로그램/평방센티미터)
            <img id="55828935"></img>
        """;

        String result = aa.replaceAll("<img\\s+[^>]*>\\s*</img>", "");


        System.out.println(result);

    }

}
