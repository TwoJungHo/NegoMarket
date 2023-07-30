import React from 'react'
import { fetch_General } from '../../networkFns/fetchFns'
import { API_URL } from '../../Constatns'

function SellStateChangerComp() {

    
const handleOnSaleButton = () => {

fetch_General("PUT", `${API_URL}/sell-service/sells/onsale`)

}




  return (
    <div>
<button onClick={handleOnSaleButton}>판매중</button>
<button>예약됨</button>
<button>판매완료</button>
    </div>
  )
}

export default SellStateChangerComp