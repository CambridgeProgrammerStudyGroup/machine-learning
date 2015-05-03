module Persistence

open System
open System.IO
open System.Text.RegularExpressions

let netAsText net = 
    let join sep (strings : string list) = String.Join(sep, strings)
    let weightAsText weight = sprintf "%.17f" weight
    let neuronAsText neuron = "[" + (neuron |> List.map weightAsText |>  join "; ") + "]" 
    let layerAsText layer = "  [\r\n    " + (layer |> List.map neuronAsText |> join ";\r\n    ") + "\r\n  ]"
    "[\r\n" + (net |> List.map layerAsText |> join ";\r\n") + "\r\n]"

let insideBracketsRx = Regex(@"(?<=^\s*\[).*(?=\]\s*$)", RegexOptions.Singleline ||| RegexOptions.Compiled)
let insideBrackets text = insideBracketsRx.Match(text).Value
let textAsNet text = 
    let textAsNeuron text =         
        [for mtch in Regex.Matches(text, "-?\d\.\d+") -> float mtch.Value] 
    let textAsLayer text =
        Regex.Split((insideBrackets text), @"(?<=\]);") |> Array.map textAsNeuron |> List.ofArray
    Regex.Split((insideBrackets text), @"(?<=  \]);") |> Array.map textAsLayer |> List.ofArray


let private appRoot =  DirectoryInfo(AppDomain.CurrentDomain.BaseDirectory).Parent.Parent

let private getChildDirectory childName = 
    Path.Combine(appRoot.FullName, childName)
    |> Directory.CreateDirectory
    |> (fun di -> di.FullName)
    
let private stateDir = getChildDirectory "state"

let private fileBase sessionName = 
    Path.Combine(stateDir, sessionName)

let dataDir = getChildDirectory "data"

let logFile = Path.Combine(stateDir, "log.txt") 
let writeLog parts =
    use stream = new FileStream(logFile, FileMode.OpenOrCreate)
    stream.Seek(0L, SeekOrigin.End) |> ignore
    use writer = new StreamWriter(stream)
    let timeStamp = DateTime.UtcNow.ToString("s")
    writer.WriteLine(String.Join("|", timeStamp::(parts |> List.map (string))))
    writer.Flush()
    stream.Flush()
    
let save name net =
    let writeFile path text =
        use stream = new FileStream(path, FileMode.CreateNew)
        use writer = new StreamWriter(stream)
        writer.Write (text : string)
        writer.Flush()
        stream.Flush()
    let fileBase = fileBase name
    let txtFile = fileBase + ".txt"
    let bckFile = fileBase + ".bck"
    let tmpFile = fileBase + ".tmp"
    writeFile tmpFile (netAsText net)
    File.Delete bckFile
    if File.Exists txtFile then File.Move(txtFile, bckFile)
    File.Move(tmpFile, txtFile)

let tryLoad name =
    let file = (fileBase name) + ".txt"
    match File.Exists file with
    | false -> None
    | true -> Some (textAsNet (File.ReadAllText file))
        

    
