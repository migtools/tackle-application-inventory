#
# Copyright © 2021 the Konveyor Contributors (https://konveyor.io/)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

IMAGE_ID=$(podman images --filter=reference='localhost/'$USER'/'$1':'$2'-'$3 -n --format {{.ID}})
TAR_NAME=$1'_'$IMAGE_ID'_'$(date +%Y%m%d%H%M%S)'.tar'
TAR_PATH=/tmp/$TAR_NAME

if command -v minikube &> /dev/null
then
  if minikube status && eval $(minikube -p minikube podman-env)
  then
    echo 'Evaluated minikube environment variables'
    podman save -o $TAR_PATH $IMAGE_ID
    echo 'Saved temporary image '$IMAGE_ID' to file '$TAR_PATH
    podman-remote load -i $TAR_PATH $USER'/'$1':'$2'-'$3
    echo 'Loaded into minikube the image '$USER'/'$1':'$2'-'$3
    rm $TAR_PATH
    echo 'Removed file '$TAR_PATH
    kubectl rollout restart deployment application-inventory
  else
    echo 'Minikube not running: skip deployment'
  fi
else
  echo 'Minikube not installed'
fi