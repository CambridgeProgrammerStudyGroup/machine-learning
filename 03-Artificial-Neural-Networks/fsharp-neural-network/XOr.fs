module XOr

open ANN
open Training

let xorCases = [|
    { input=[0.0; 0.0]; target =[0.0] };
    { input=[0.0; 1.0]; target =[1.0] };
    { input=[1.0; 0.0]; target =[1.0] };
    { input=[1.0; 1.0]; target =[0.0] };
|]

// Train XOr
let xor() =
    let net = createNet [2; 2; 1]
    let checkCorrect result =
        match result.sample.target.Head with
        | 0. -> result.output.Head < 0.1
        | 1. -> result.output.Head > 0.9
        | _ -> failwith "doh!"
    let checkDone result =
        result.cost < 0.001
    let testNet _ _=  (0, 0)
    let learnRate = 0.9
    trainUntil net learnRate xorCases checkCorrect checkDone testNet |> ignore
    ()


