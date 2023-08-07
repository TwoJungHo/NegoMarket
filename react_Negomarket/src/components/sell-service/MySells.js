import React, { useEffect, useState } from "react";
import { fetch_General } from "../../networkFns/fetchFns";
import MySellComp from "./MySellComp";
import { API_URL } from '../../Constants';
function MySells() {
  const [mySellList, setMySellList] = useState([]);

  useEffect(() => {
    (async () => {
      const sellList = await fetch_General(
        "GET",
        `${API_URL}/sell-service/sells/mylist`
      );
      setMySellList(sellList)})();
    }
  , []);

  return (
    <div className="routes">
      {mySellList !==undefined && mySellList !== null && mySellList.length > 0 &&
        mySellList.map((mySell, index) => <MySellComp key={index} mySell={mySell}/>)}
    </div>
  );
}

export default MySells;
