import newslogo from './assets/logo.png'

function Card(){
    return(
        <div class="card">
            <img class="imga" src={newslogo} alt="newsapplogo"></img>
            <h2 class="titname">This is our admin pannal</h2>
            <p class="crdtxt">Admin</p>
        </div>
    );
}

export default Card