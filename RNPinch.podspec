require 'json'

package = JSON.parse(File.read(File.join(__dir__, './package.json')))

Pod::Spec.new do |s|
  s.name         = "RNPinch"
  s.version      = package['version']
  s.summary      = package['description']

  s.homepage     = package['homepage']
  s.license      = package['license']
  s.author       = 'Localz'
  s.platform     = :ios, "9.0"
  s.source       = { :git => "https://github.com/localz/react-native-pinch", :tag => "master" }
  s.source_files  = "RNPinch/*.{h,m}"
  s.requires_arc = true
  
  s.dependency "React"

end

  