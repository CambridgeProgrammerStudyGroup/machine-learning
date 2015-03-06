open System
open System.IO

type point = {x:float; y:float}          

let costOfSequence data a c =
    let costOfPoint point  =
        (point.y - ( a * point.x + c)) ** 2.0
    data |> Array.sumBy costOfPoint 

let rec climbHill func x y step = seq{
    yield (x, y, step)

    let best = 
        [(x+step, y); (x, y+step); (x-step, y); (x, y-step)]
        |> List.map (fun (x, y) -> (func x y, x, y))
        |> List.max

    yield! match best with
            | (value', x', y') when value' > func x y -> climbHill func x' y' step
            | (value', _, _)  -> climbHill func x y (step/10.0)
}  

let guess (data: point array) =
    let step = List.max <| [for d in data do yield abs d.y] 
    let first = data.[0]
    let last = data.[data.Length - 1]
    if first.y = last.y then
        (0.0, first.y, step)
    else
        let a = (last.y - first.y)/(last.x - first.x)
        let c = first.y - (first.x * a)
        (a, c, step)    


[<EntryPoint>]
let main argv = 
    Environment.CurrentDirectory <- __SOURCE_DIRECTORY__

    let readFloats file =  
        File.ReadAllLines file
        |> Array.map float

    let sampleData = 
        Array.zip (readFloats @"..\data\ex2x.dat") (readFloats @"..\data\ex2y.dat") 
        |> Array.map(fun (a, h) -> {x=a; y = h})
        
    let minStep = 0.000000001
    let aGuess, cGuess, step = guess sampleData
    printfn "Starting with: a=%f c=%f step=%f" aGuess cGuess step 
    let quality a c = -(costOfSequence sampleData a c)    
    let trail = climbHill quality aGuess cGuess step
   
    let a, c, _ = trail 
                    |> Seq.where (fun (_, _, step) -> abs step < minStep)
                    |> Seq.head

    printfn ""
    printfn "Answer: a=%0.8f c=%0.8f" a c 

    Console.ReadKey() |> ignore
    0