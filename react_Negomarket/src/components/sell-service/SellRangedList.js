import React from "react";
import { useState } from "react";
import KakaoMapSellList from "../kakaoMapComponents/KakaoMapSellList";
import RangedSellComp from "./RangedSellComp";

function SellRangedList() {
  const [rangedSellList, setRangedSellList] = useState([]);

  return (
    <div className="routes">
      
        <KakaoMapSellList setRangedSellList={setRangedSellList} />
   
      
        {rangedSellList.length > 0 &&
          rangedSellList.map((rangedSell, index) => (
            <RangedSellComp key={index} rangedSell={rangedSell} />
          ))}
      
    </div>
  );
}

export default SellRangedList;
