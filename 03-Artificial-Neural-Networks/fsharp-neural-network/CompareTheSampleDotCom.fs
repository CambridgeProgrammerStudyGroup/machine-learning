module CompareTheSampleDotCom

open ANN

let compare() =
    let sample = { input = [1.0; 0.0; 1.0]; target = [1.0]}
    let net = [[[-0.4; 0.2; 0.4; -0.5; ]; [0.2; -0.3; 0.1; 0.2] ];
                    [[0.1; -0.3; -0.2]]]
    let expectedValues = [[1.0; 0.0; 1.0]; [0.3318122278; 0.5249791875]; [0.4738888988]]
    let expectedNet = [[[-0.4078521058; 0.1921478942; 0.4; -0.5078521058];
                        [0.1941121234; -0.3058878766; 0.1; 0.1941121234]];
                        [[0.2180521704; -0.2608288463; -0.1380250675]]]   
    let learnRate = 0.9
    let newNet = (backPropagate net learnRate sample).newNet
    printfn "newNet   \r\n%A" newNet
    printfn "expected \r\n%A" expectedNet
