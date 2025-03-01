import newslogo from './assets/logo.jpg'

function Card(){
    return(
        <div class="card">
            <img class="imga" src={newslogo} alt="newsapplogo"></img>
            <h2 className='titname'>This is our admin pannal</h2>
            <p>Admin</p>
        </div>
    );
}

export default Card