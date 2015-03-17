import AssemblyKeys._ // put this at the top of the file

assemblySettings

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
{
  case "log4j.properties" => MergeStrategy.first
  case "ECLIPSEF.RSA" => MergeStrategy.first
  case x => old(x)
}
}
