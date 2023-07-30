import React, { useEffect, useRef, useState } from 'react'
import { fetchFn } from '../../NetworkUtils';
import { API_URL } from '../../Constants';

function SellUpdate(props) {
    
  
    const [previewImage, setPreviewImage] = useState(null);

    const imgFileInputRef = useRef();
    const productNameRef = useRef();
    const priceRef = useRef();
    const sellStateRef = useRef();

    useEffect(() => {
        
        setPreviewImage(`${API_URL}/sell-service/img/${props.sellId}`);
    },[])

    function handleInputChange() {

        const file = imgFileInputRef.current.files[0];
        const productName = productNameRef.current.value;
        const price = priceRef.current.value;
        const sellState = sellStateRef.current.value;

        props.setSellData.setImgFile(file);
        props.setSellData.setProductName(productName);
        props.setSellData.setPrice(price);
        props.setSellData.setSellState(sellState);

               // 그림 파일 입력시 미리 보기 생성
        if (file) {
            const reader = new FileReader();

            // file 읽기가 완료될 경우 실행되는 콜백함수
            reader.onloadend = () => {
                setPreviewImage(reader.result);
            };

            // 선택된 파일을 읽어서 파일의 내용이 직접 포함된 URL로 변환
            reader.readAsDataURL(file);
        } else {
            setPreviewImage(null);
        }

    }
    const [SelectBtn, setSelectBtn] = useState();
    const onClick = (event) => {
        const name = event.target.name;

        setSelectBtn(name);
        props.setSellData.setSellState(name);
        console.log(name)
        }



    return (
        <>
            <div style={{height: '220px'}}>
                <div className='imageBox' style={{ verticalAlign: '-39px',display: 'inline-block', height: 'auto', width: '41%', textAlign: 'center', margin: '20px 5px 0px 0px' }}><img style={{ maxHeight: '200px' , maxWidth: '400px',}} src={previewImage? previewImage : '/img/nego1.png'} alt='preview' /></div>
                <div style={{display: 'inline-block'}}>
                    <div style={{margin : '5px'}}><input style={{width: '498px', height: '31px', marginTop: '0.5%'}} className='border' type="file" ref={imgFileInputRef} onChange={handleInputChange}></input></div>
                    <div style={{margin : '5px'}}><input style={{width: '498px', height: '30px'}} className='border' type="text" defaultValue={props.getSellData.productName} ref={productNameRef} onChange={handleInputChange} placeholder='상품명'></input></div>
                    <div style={{margin : '5px'}}><input style={{width: '498px', height: '30px'}} className='border' type="text" defaultValue={props.getSellData.price} ref={priceRef} onChange={handleInputChange} placeholder='가격'></input></div>
                
                <div  style={{marginLeft: '1%',marginBottom: '1%'}}>
                       
                       <button style={{marginRight: '1%',color: 'orange',borderColor: 'orange'}} name='ON_SALE' onClick={onClick} className={SelectBtn === 'ON_SALE' ? 'button_active' : 'button'}>ON_SALE</button>
                       <button style={{marginRight: '1%',color: 'green',borderColor: 'green'}} name='RESERVED' onClick={onClick} className={SelectBtn === 'RESERVED' ? 'button_active' : 'button'}>RESERVED</button>
                       <button style={{marginRight: '1%'}} name='SOLD_OUT' onClick={onClick} className={SelectBtn === 'SOLD_OUT' ? 'button_active' : 'button'}>SOLD_OUT</button>
                       <button style={{marginRight: '1%',color: 'red',borderColor: 'red'}} name='FINISHED' onClick={onClick} className={SelectBtn === 'FINISHED' ? 'button_active' : 'button'}>FINISHED</button>
       
                   </div>
            
                </div> 
                </div>
                
        </>
    )
}

export default SellUpdate