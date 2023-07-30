import React, { useEffect, useState } from 'react'

function GetRate(props) {
const [rate, setRate] = useState(0.5);



  function onClickHandler(index) {

    const score = index / 2;
    props.parentStateFunction(score);
    setRate(index/ 2);

  }

  useEffect(() => {

  })

  function ReviewRateImg(index) {

    if (index % 2 !== 0 && (index / 2) <= rate) {
      return <button key={index} onClick={() => onClickHandler(index)} className='star-left-full' />
    }
    else if (index % 2 !== 0 && (index / 2) > rate) {
      return <button key={index} onClick={() => onClickHandler(index)} className='star-left-empty' />
    }
    else if (index % 2 === 0 && (index / 2) <= rate) {
      return <button key={index} onClick={() => onClickHandler(index)} className='star-right-full' />
    }
    else if (index % 2 === 0 && (index / 2) > rate) {
      return <button key={index} onClick={() => onClickHandler(index)} className='star-right-empty' />
    }


  };



  return (
    <div>
      {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((x) =>

        ReviewRateImg(x))}
    </div>
  );






}

export default GetRate