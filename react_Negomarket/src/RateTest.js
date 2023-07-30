import React, { useState } from 'react'
import GetRate from './GetRate';

function RateTest() {
const [score, setScore] = useState();

console.log(score);


  return (
    <div>
        <GetRate parentStateFunction={setScore}/>
    </div>
  )
}

export default RateTest